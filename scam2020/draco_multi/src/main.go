package main

import (
	"bufio"
	"flag"
	"fmt"
	"log"
	"math"
	"os"
	"runtime/pprof"
	"strconv"
	"strings"
	"time"

	"github.com/project-draco/moea"
	"github.com/project-draco/moea/binary"
	"github.com/project-draco/moea/integer"
	"github.com/project-draco/moea/nsgaii"
	"github.com/project-draco/moea/nsgaiii"
)

func main() {
	prepeat := flag.Int("repeat", 0, "Repeat")
	pmulti := flag.Bool("multi", false, "Multi-objective")
	pmany := flag.Bool("many", false, "Many-objective")
	cpuprofile := flag.String("cpuprofile", "", "write cpu profile to file")
	memprofile := flag.String("memprofile", "", "write mem profile to file")
	flag.Parse()
	if *cpuprofile != "" {
		f, err := os.Create(*cpuprofile)
		if err != nil {
			log.Fatal(err)
		}
		pprof.StartCPUProfile(f)
		defer func() {
			pprof.StopCPUProfile()
			f.Close()
		}()
	}
	type edge struct{ source, destination int }
	graph := map[edge]float64{}
	vertices := map[string]int{}
	names := map[int]string{}
	// read MDG from stdin
	indexOf := func(name string) int {
		if index, ok := vertices[name]; ok {
			return index
		}
		index := len(vertices)
		vertices[name] = index
		names[index] = name
		return index
	}
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		var arr []string
		t := strings.TrimSpace(strings.Replace(scanner.Text(), "  ", " ", -1))
		if len(t) == 0 {
			continue
		}
		if strings.Index(t, "\t") != -1 {
			arr = strings.Split(t, "\t")
		} else {
			arr = strings.Split(t, " ")
		}
		weigth := 1.0
		if len(arr) > 2 {
			w, err := strconv.Atoi(arr[2])
			if err != nil {
				fmt.Fprintln(os.Stderr, err)
				return
			}
			weigth = float64(w)
		}
		graph[edge{indexOf(arr[0]), indexOf(arr[1])}] = weigth
	}
	if scanner.Err() != nil {
		fmt.Fprintln(os.Stderr, scanner.Err())
		return
	}
	type edgeWithWeight struct {
		edge   edge
		weight float64
	}
	graphArr := make([]edgeWithWeight, len(graph))
	i := 0
	for k, v := range graph {
		graphArr[i] = edgeWithWeight{edge: k, weight: v}
		i++
	}
	// each individual has len(vertices) values representing the cluster each vertex belongs
	lengths := make([]int, len(vertices))
	bounds := make([]binary.Bound, len(vertices))
	ibounds := make([]integer.Bound, len(vertices))
	lbits := int(math.Ceil(math.Log2(float64(len(vertices))/2) - 0.5))
	lbound := strings.Repeat("0", lbits)
	ubound := fmt.Sprintf("%b", len(vertices)/2)
	for i := 0; i < len(vertices); i++ {
		lengths[i] = lbits
		bounds[i] = binary.Bound{lbound, ubound}
		ibounds[i] = integer.Bound{0, len(vertices) / 2}
	}
	// set crossover probability according to Brian S. Mitchel (2002, p 93)
	var cp float64
	if len(vertices) <= 100 {
		cp = 0.8
	} else if len(vertices) >= 1000 {
		cp = 1.0
	} else {
		cp = 0.8 + 0.2*(float64(len(vertices))-100)/899
	}
	// set population size and max generations adapted from to Ivan Candela et al. (2016, p 93)
	// except for the limit of 300, that is 150 in the referenced work, and the limit of 3000
	// that is new
	var ps, mg int
	if len(vertices) > 10000 {
		ps = len(vertices) / 4
		mg = len(vertices)
	} else if len(vertices) > 3000 {
		ps = len(vertices) / 2
		mg = 5 * len(vertices)
	} else if len(vertices) > 300 {
		ps = 1 * len(vertices)
		mg = 20 * len(vertices)
	} else {
		ps = 2 * len(vertices)
		mg = 50 * len(vertices)
	}
	// This is the typical mutatation rate for binary encoding according to Brian S. Mitchel (2002, p 93)
	mp := 16.0 / (math.Sqrt(float64(len(vertices))) * 1000)
	var start time.Time
	var selection moea.SelectionOperator
	var nsgaiii = &nsgaiii.NsgaIIISelection{ReferencePointsDivision: 3}
	var nsgaii = &nsgaii.NsgaIISelection{}
	newConfig := func() *moea.Config {
		// Objective function computes Turbo MQ metric according to Brian S. Mitchel (2002, pp 65-67)
		?? := make([]float64, len(vertices))
		?? := make([]float64, len(vertices))
		c := make([]int64, len(vertices))
		k := make([]float64, len(vertices))
		objectiveFunc := func(individual moea.Individual) []float64 {
			for i, _ := range k {
				k[i] = 0
			}
			for i := 0; i < len(vertices); i++ {
				??[i] = 0
				??[i] = 0
				c[i] = individual.Value(i).(binary.BinaryString).Int().Int64()
				// c[i] = int64(individual.Value(i).(int))
				k[c[i]]++
			}
			min, max, cc := math.MaxFloat64, 0.0, 0.0
			for _, q := range k {
				if q == 0 {
					continue
				}
				cc++
				if q < min {
					min = q
				}
				if q > max {
					max = q
				}
			}
			f1, f2 := 0.0, 0.0
			for _, e := range graphArr {
				i := c[e.edge.source]
				j := c[e.edge.destination]
				if i == j {
					??[i] += e.weight
					f1++
				} else {
					??[i] += e.weight
					??[j] += e.weight
					f2 += 2
				}
			}
			mq := 0.0
			for i := 0; i < len(vertices); i++ {
				if ??[i] > 0 {
					mq += 2 * ??[i] / (2*??[i] + ??[i])
				}
			}
			return []float64{-mq, -f1, f2, -cc, max - min}
		}
		rng := moea.NewXorshiftWithSeed(uint32(time.Now().UTC().UnixNano()))
		bp := binary.NewRandomBinaryPopulation(ps, lengths, nil /*bounds*/, rng)
		// _ /*ip :*/ = integer.NewRandomIntegerPopulation(ps, len(vertices), ibounds, rng)
		if *pmany {
			selection = nsgaiii
		} else if *pmulti {
			selection = nsgaii
		} else {
			selection = &moea.TournamentSelection{10}
		}
		fmt.Fprintln(os.Stderr, "About to create config")
		return &moea.Config{
			Algorithm:             moea.NewSimpleAlgorithm(selection, &moea.FastMutation{}),
			Population:            bp,
			NumberOfObjectives:    5,
			NumberOfValues:        len(vertices),
			ObjectiveFunc:         objectiveFunc,
			MaxGenerations:        mg,
			CrossoverProbability:  cp,
			MutationProbability:   mp,
			RandomNumberGenerator: rng,
			OnGenerationFunc: func(i int, r *moea.Result) {
				if i == 0 {
					fmt.Fprintf(os.Stderr, "start")
				}
				if (i+1)%100 == 0 {
					fmt.Fprintf(os.Stderr, ".")
				}
				if (i+1)%1000 == 0 {
					fmt.Fprintf(os.Stderr, "%s", time.Since(start).Round(100*time.Millisecond))
				}
				if (i+1)%5000 == 0 {
					fmt.Println("")
				}
			},
		}
	}
	fmt.Fprintf(os.Stderr, "Max Generations: %v, Population Size: %v, Individual Size: %v, Variables: %v\n",
		mg, ps, lbits*ps, len(lengths))
	start = time.Now()
	result, err := moea.RunRepeatedly(newConfig, *prepeat)
	if err != nil {
		fmt.Fprintln(os.Stderr, err)
		return
	}
	clusters := map[int64][]string{}
	for i := 0; i < len(vertices); i++ {
		c := result.BestIndividual.Value(i).(binary.BinaryString).Int().Int64()
		// c := int64(result.BestIndividual.Value(i).(int))
		if _, ok := clusters[c]; !ok {
			clusters[c] = []string{}
		}
		clusters[c] = append(clusters[c], names[i])
	}
	fmt.Println("digraph {")
	for k, v := range clusters {
		fmt.Printf("subgraph cluster%v {\n", k)
		for _, n := range v {
			fmt.Printf("\"%v\";\n", n)
		}
		fmt.Println("}")
	}
	fmt.Println("}")
	fmt.Fprintln(os.Stderr, "")
	for i, individual := range result.Individuals {
		fmt.Printf("[")
		fmt.Fprintf(os.Stderr, "mq: %.4f,", individual.Objective[0])
		fmt.Fprintf(os.Stderr, "f1: %.4f,", individual.Objective[1])
		fmt.Fprintf(os.Stderr, "f2: %.4f,", individual.Objective[2])
		fmt.Fprintf(os.Stderr, "cc: %.4f,", individual.Objective[3])
		fmt.Fprintf(os.Stderr, "dif: %.4f,", individual.Objective[4])
		fmt.Fprintf(os.Stderr, "]")
		fmt.Fprintf(os.Stderr, " %v\n", nsgaii.Rank[i])
	}
	fmt.Fprintln(os.Stderr, "")
	fmt.Fprintln(os.Stderr, "")
	fmt.Fprintln(os.Stderr, "BEST OBJECTIVES")
	fmt.Fprintln(os.Stderr, "mq: ", result.BestObjective[0])
	fmt.Fprintln(os.Stderr, "f1: ", result.BestObjective[1])
	fmt.Fprintln(os.Stderr, "f2: ", result.BestObjective[2])
	fmt.Fprintln(os.Stderr, "cc: ", result.BestObjective[3])
	fmt.Fprintln(os.Stderr, "dif: ", result.BestObjective[4])
	if *memprofile != "" {
		f, err := os.Create(*memprofile)
		if err != nil {
			log.Fatal(err)
		}
		pprof.WriteHeapProfile(f)
		f.Close()
	}
}
