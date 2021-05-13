package main

import (
	"bufio"
	// "flag"
	"fmt"
	// "log"
	"math/rand"
	"os"
	"sort"
	// "runtime/pprof"
	"runtime"
	"strconv"
	"strings"
	"time"

	"github.com/exascience/pargo/parallel"
)

type edge struct{ source, destination int }
type edgeWithWeight struct {
		edge   edge
		weight float64
	}
type clusterInfo struct{ 
	numberOfVertexes int
	IntraEdgeWeightSum, InterEdgeWeightSum  float64
}
type vertexInfo struct{ start, size, cluster int }
type ByEdgeSource []edgeWithWeight

func (a ByEdgeSource) Len() int            	{ return len(a) }

func (a ByEdgeSource) Less(i, j int) bool { return a[i].edge.source < a[j].edge.source }

func (a ByEdgeSource) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }

func calculateMQ(clustersArr []clusterInfo) float64 {
	MQ := 0.0
	parallel.Range(0, len(clustersArr), runtime.GOMAXPROCS(0),func(low, high int) {
	for i := low; i < high; i++ {
		if clustersArr[i].numberOfVertexes > 0 {
			currentintraEdgeWeightSum := clustersArr[i].IntraEdgeWeightSum
			currentinterEdgeWeightSum := clustersArr[i].InterEdgeWeightSum
			if currentintraEdgeWeightSum > 0.0 {
				MQ += 2.0*currentintraEdgeWeightSum / ((2*currentintraEdgeWeightSum) + currentinterEdgeWeightSum)
			}
		}
	}},)
	return MQ
}

func copyClustersArr(clustersArrA, clustersArrB []clusterInfo)  {
	parallel.Range(0, len(clustersArrA), runtime.GOMAXPROCS(0),func(low, high int) {
	for i := low; i < high; i++ {
		clustersArrA[i].numberOfVertexes = clustersArrB[i].numberOfVertexes
		clustersArrA[i].IntraEdgeWeightSum = clustersArrB[i].IntraEdgeWeightSum
		clustersArrA[i].InterEdgeWeightSum = clustersArrB[i].InterEdgeWeightSum
	}},)
}

func copyCluster(clusterA, clusterB clusterInfo)  {
	clusterA.numberOfVertexes = clusterB.numberOfVertexes
	clusterA.IntraEdgeWeightSum = clusterB.IntraEdgeWeightSum
	clusterA.InterEdgeWeightSum = clusterB.InterEdgeWeightSum
}

func copyVertexArr(vertexArrA, vertexArrB []vertexInfo)  {
	parallel.Range(0, len(vertexArrA), runtime.GOMAXPROCS(0),func(low, high int) {
	for i := low; i < high; i++ {
		vertexArrA[i].start = vertexArrB[i].start
		vertexArrA[i].size = vertexArrB[i].size
		vertexArrA[i].cluster = vertexArrB[i].cluster
	}},)
}

func CreateNewCluster (clustersArr []clusterInfo) int{
	index_of_new_cluster := -1
	for j := range clustersArr {
		if clustersArr[j].numberOfVertexes == 0 {
			index_of_new_cluster = j
			break
		}
	}
	if index_of_new_cluster == -1 {
		fmt.Fprintln(os.Stderr, "Err: could not create new cluster")
		return -1
	}
	clustersArr[index_of_new_cluster].IntraEdgeWeightSum = 0
	clustersArr[index_of_new_cluster].InterEdgeWeightSum = 0
	return index_of_new_cluster
}

func calculateCF (cluster clusterInfo) float64 {
	return (2.0*cluster.IntraEdgeWeightSum) / ((2*cluster.IntraEdgeWeightSum) + cluster.InterEdgeWeightSum)
}

func writeOutputFile(vertexArr []vertexInfo, names map[int]string, graphArr []edgeWithWeight) {
	fmt.Fprintln(os.Stderr, "Writing output file...")
	var buf strings.Builder
	buf.WriteString("strict graph {\nlayout=fdp\n")
	for i:= range graphArr {
		buf.WriteString(fmt.Sprintf("\t\"%v\"--\"%v\"\n", names[graphArr[i].edge.source], names[graphArr[i].edge.destination]))
	}
	for i:= range vertexArr {
		buf.WriteString(fmt.Sprintf("subgraph cluster%v {\"%v\"}\n", vertexArr[i].cluster, names[i]))
	}
	buf.WriteString("}\n")

	fmt.Fprint(os.Stdout, buf.String())
}

func main() {
	var start time.Time
		start = time.Now()


	// Initial variables and funtions to load the mdg
	graph := map[edge]float64{}
	vertices := map[string]int{}
	names := map[int]string{}

	// Auxiliar function to access the index of a given module name
	indexOf := func(name string) int {
		if index, ok := vertices[name]; ok {
			return index
		}
		index := len(vertices)
		vertices[name] = index
		names[index] = name
		return index
	}

	// Read the mdg file and store information in an array
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
		weight := 1.0
		if len(arr) > 2 {
			w, err := strconv.Atoi(arr[2])
			if err != nil {
				fmt.Fprintln(os.Stderr, err)
				return
			}
			weight = float64(w)
		}

		if _, ok := graph[edge{indexOf(arr[0]), indexOf(arr[1])}]; ok {
			graph[edge{indexOf(arr[0]), indexOf(arr[1])}] += weight
			graph[edge{indexOf(arr[1]), indexOf(arr[0])}] += weight
		} else {
			graph[edge{indexOf(arr[0]), indexOf(arr[1])}] = weight
			graph[edge{indexOf(arr[1]), indexOf(arr[0])}] = weight
		}
	}
	if scanner.Err() != nil {
		fmt.Fprintln(os.Stderr, scanner.Err())
		return
	}

	graphArr := make([]edgeWithWeight, len(graph))
	i := 0
	for k, v := range graph {
		graphArr[i] = edgeWithWeight{edge: k, weight: v}
		i++
	}
	sort.Sort(ByEdgeSource(graphArr))

    clustersArr := make([]clusterInfo, len(names))
    clustersArrCopy := make([]clusterInfo, len(names))
	for i := range clustersArr {
		clustersArr[i] = clusterInfo{numberOfVertexes: 1, IntraEdgeWeightSum: 0, InterEdgeWeightSum: 0}
	}

    vertexArr := make([]vertexInfo, len(names))
    vertexArrCopy := make([]vertexInfo, len(names))
	for i, _ := range graphArr {
		clustersArr[graphArr[i].edge.source].InterEdgeWeightSum += graphArr[i].weight
		if i == 0 {
			vertexArr[graphArr[i].edge.source] = vertexInfo{start: 0, size: 1, cluster: graphArr[i].edge.source}
		} else {
			if graphArr[i].edge.source !=  graphArr[i-1].edge.source {
				vertexArr[graphArr[i].edge.source] = vertexInfo{start: i, size: 1, cluster: graphArr[i].edge.source}
			} else {
				vertexArr[graphArr[i].edge.source].size++
			}
		}
	}

	numberOfClusters := len(vertexArr)
	bestGlobalMQ := -999999.0


	copyClustersArr(clustersArrCopy, clustersArr)
	copyVertexArr(vertexArrCopy, vertexArr)

	// Constructive Agglomerative MQ (CAMQ):
	for {
		mergeI := 0
		mergeJ := 1
		mergeIntraEdgeWeightSum := 0.0
		mergeInterEdgeWeightSum := 0.0
		bestLocalMQ := -999999.0
		parallel.Range(0, len(clustersArrCopy), runtime.GOMAXPROCS(0), func(low, high int) {
		for i := low; i < high; i++ {
			for j := i+1; j < len(clustersArrCopy); j++ {
				if clustersArrCopy[i].numberOfVertexes > 0 && clustersArrCopy[j].numberOfVertexes > 0 {
					intraEdgeWeightSumI := clustersArrCopy[i].IntraEdgeWeightSum
					interEdgeWeightSumI := clustersArrCopy[i].InterEdgeWeightSum
					initialMQi := (2*intraEdgeWeightSumI) / ((2*intraEdgeWeightSumI) + interEdgeWeightSumI)
					intraEdgeWeightSumJ := clustersArrCopy[j].IntraEdgeWeightSum
					interEdgeWeightSumJ := clustersArrCopy[j].InterEdgeWeightSum
					initialMQj := (2*intraEdgeWeightSumJ) / ((2*intraEdgeWeightSumJ) + interEdgeWeightSumJ)
					initialMQ := initialMQi + initialMQj
					for k := range vertexArrCopy {
						if vertexArrCopy[k].cluster == i {
							for p := 0; p < vertexArrCopy[k].size; p++ {
								q := vertexArrCopy[k].start
								if vertexArrCopy[graphArr[q+p].edge.destination].cluster == j {
									intraEdgeWeightSumI+=graphArr[q+p].weight
									interEdgeWeightSumI-=graphArr[q+p].weight
									interEdgeWeightSumJ-=graphArr[q+p].weight
								}
							}
						}
					}
					intraEdgeWeightSum := intraEdgeWeightSumI + intraEdgeWeightSumJ
					interEdgeWeightSum := interEdgeWeightSumI + interEdgeWeightSumJ
					finalMQ := (2*intraEdgeWeightSum) / ((2*intraEdgeWeightSum) + interEdgeWeightSum)
					gainMQ := finalMQ - initialMQ
					if gainMQ > bestLocalMQ {
						bestLocalMQ = gainMQ
						mergeI = i
						mergeJ = j
						mergeIntraEdgeWeightSum = intraEdgeWeightSum 
						mergeInterEdgeWeightSum = interEdgeWeightSum 
					}
				}
			}
		}},)
		clustersArrCopy[mergeI].IntraEdgeWeightSum = mergeIntraEdgeWeightSum
		clustersArrCopy[mergeI].InterEdgeWeightSum = mergeInterEdgeWeightSum
		parallel.Range(0, len(vertexArrCopy), runtime.GOMAXPROCS(0),func(low, high int) {
		for i := low; i < high; i++ {
			if vertexArrCopy[i].cluster == mergeJ {
				vertexArrCopy[i].cluster = mergeI
				clustersArrCopy[mergeI].numberOfVertexes++
				clustersArrCopy[mergeJ].numberOfVertexes--
			}
		}},)
		currentGlobalMQ := calculateMQ(clustersArrCopy)
		if currentGlobalMQ > bestGlobalMQ {
			bestGlobalMQ = currentGlobalMQ
			copyClustersArr(clustersArr, clustersArrCopy)
			copyVertexArr(vertexArr, vertexArrCopy)
		}
		numberOfClusters--
		if numberOfClusters == 1 {
			break
		}
	}

	destroyArr := make([]int, len(vertexArr)/5)
	rand.Seed(time.Now().Unix())

	no_improvement_threshold := 1000
	// Iterations with no_improvement_threshold = 1000 and configuration MIXED with two repair methods: RGBIR and RGBI
	numberOfNoImprovementIteractions := 0
	for {
		copyClustersArr(clustersArrCopy, clustersArr)
		copyVertexArr(vertexArrCopy, vertexArr)

		// Destructive Random (DR) with Degree = 0.1
		destroyArr = rand.Perm(len(vertexArrCopy))[0:len(vertexArrCopy)/5]

		// For every to be destroyed vertex
		for i:=0; i < len(destroyArr); i++{
			// For every neighbor of the destroyed vertex 
			for j := vertexArrCopy[destroyArr[i]].start; j <  vertexArrCopy[destroyArr[i]].start + vertexArrCopy[destroyArr[i]].size; j++ {
				// Update intra edge weight sum
				if vertexArrCopy[graphArr[j].edge.destination].cluster == vertexArrCopy[destroyArr[i]].cluster {
					clustersArrCopy[vertexArrCopy[destroyArr[i]].cluster].IntraEdgeWeightSum -= graphArr[j].weight
					clustersArrCopy[vertexArrCopy[destroyArr[i]].cluster].InterEdgeWeightSum += graphArr[j].weight
				} else {
					clustersArrCopy[vertexArrCopy[destroyArr[i]].cluster].InterEdgeWeightSum -= graphArr[j].weight
				}
			}
			clustersArrCopy[vertexArrCopy[destroyArr[i]].cluster].numberOfVertexes--
			vertexArrCopy[destroyArr[i]].cluster = -1
		}


		// Repair Greedy Best Improvement Random (RGBIR):
		for i:=0; i < len(destroyArr); i++{
			index_of_new_cluster := CreateNewCluster(clustersArrCopy)
			// Put the current destroyed vertex inside new cluster
			for j := vertexArrCopy[destroyArr[i]].start; j <  vertexArrCopy[destroyArr[i]].start + vertexArrCopy[destroyArr[i]].size; j++ {
				// Update inter edge weight sum
				clustersArrCopy[index_of_new_cluster].InterEdgeWeightSum += graphArr[j].weight
			}
			clustersArrCopy[index_of_new_cluster].numberOfVertexes++
			vertexArrCopy[destroyArr[i]].cluster = index_of_new_cluster

			// Test if merging new cluster with other will improve de MQ value
			bestGainCFs := 0.0
			var mergeCluster clusterInfo
			var mergeClusterIndex int
			mergeClusterBool := false
			parallel.Range(0, len(clustersArrCopy), runtime.GOMAXPROCS(0), func(low, high int) {
			for j := low; j < high; j++ {
				// For all other clusters, but the new cluster
				if vertexArrCopy[destroyArr[i]].cluster != j && clustersArrCopy[j].numberOfVertexes > 0 {
					initialCFs := calculateCF(clustersArrCopy[vertexArrCopy[destroyArr[i]].cluster]) + calculateCF(clustersArrCopy[j])
					mergeClusterCopy := clustersArrCopy[j]
					mergeClusterCopy.numberOfVertexes++

					// For every neighbor of the unique vertex inside the new cluster
					for p := vertexArrCopy[destroyArr[i]].start; p < vertexArrCopy[destroyArr[i]].start + vertexArrCopy[destroyArr[i]].size; p++ {
						if vertexArrCopy[graphArr[p].edge.destination].cluster == j {
							mergeClusterCopy.IntraEdgeWeightSum += graphArr[p].weight
							mergeClusterCopy.InterEdgeWeightSum -= 2*(graphArr[p].weight)
						}
					}
					mergeClusterCopy.InterEdgeWeightSum += clustersArrCopy[index_of_new_cluster].InterEdgeWeightSum

					// Calculate merge MQ gain
					finalCFs := calculateCF(mergeClusterCopy)
					gainCFs := finalCFs - initialCFs

					if gainCFs > bestGainCFs {
						mergeClusterBool = true
						bestGainCFs = gainCFs
						mergeCluster = mergeClusterCopy
						mergeClusterIndex = j
					}
				}
			}},)
			// Merge new cluster to best cluster if necessary
			if mergeClusterBool {
				clustersArrCopy[vertexArrCopy[destroyArr[i]].cluster].numberOfVertexes--
				clustersArrCopy[mergeClusterIndex].numberOfVertexes++
				vertexArrCopy[destroyArr[i]].cluster = mergeClusterIndex
				clustersArrCopy[mergeClusterIndex] = mergeCluster
			}
		}

		currentIterGlobalMQ := calculateMQ(clustersArrCopy)

		if currentIterGlobalMQ > bestGlobalMQ {
			// fmt.Fprintln(os.Stderr, "iter change", bestGlobalMQ, currentIterGlobalMQ)
			bestGlobalMQ = currentIterGlobalMQ
			copyClustersArr(clustersArr, clustersArrCopy)
			copyVertexArr(vertexArr, vertexArrCopy)
			numberOfNoImprovementIteractions = 0
		} else {
			numberOfNoImprovementIteractions++
		}
		if numberOfNoImprovementIteractions == no_improvement_threshold {
			break
		}
	}

	fmt.Fprintln(os.Stderr, "BEST MQ = ", bestGlobalMQ)
	writeOutputFile(vertexArr, names, graphArr)
	fmt.Fprintln(os.Stderr, time.Since(start).Round(100*time.Millisecond))

}
