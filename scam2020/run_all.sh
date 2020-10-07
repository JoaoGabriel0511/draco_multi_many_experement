function run_cmd {
    CMD=$1
    set -x
    ${CMD}
    set +x
}

echo "Welcome to SCM Performance Analyzer"
echo
if [ "$1" = "" ]; then
    echo "    ./run_all.sh -s/--start           "
    echo "    ./run_all.sh -p/--build           "
    echo "    ./run_all.sh -f/--force-build     "
    echo "    ./run_all.sh -p/--stop            "
    echo "    ./run_all.sh -c/--clean           "
    echo "    ./run_all.sh -c/--logs            "
    echo
else
    OPT=
    while [[ $# -gt 0 ]]
    do
        key="$1"
        case $key in
            -s|--start)
            OPT=START
            shift # past argument
            ;;
            -b|--build)
            OPT=BUILD
            shift # past argument
            ;;
            -f|--force-build)
            OPT=FORCE
            shift # past argument
            ;;
            -p|--stop)
            OPT=STOP
            shift # past argument
            ;;
            -c|--clean)
            OPT=CLEAN
            shift # past argument
            ;;
            -l|--logs)
            OPT=LOGS
            shift # past argument
            ;;
            *)    # unknown option
            POSITIONAL+=("$1") # save it in an array for later
            shift # past argument
            ;;
        esac
    done

    set -- "${POSITIONAL[@]}" # restore positional parameters
        
    echo "OPT = ${OPT}"

    if [ "$OPT" = "" ]; then
        echo "Please describe script option."
        exit 4
    elif [ "$OPT" = "START" ]; then
        mkdir -p 'draco/exp/large' 'draco/exp/medium' 'draco/exp/small' 'draco/exp/test' 'draco/log/time' 'draco/log/perf'
        run_cmd "docker-compose -f draco/docker-compose.draco.yml --compatibility up --remove-orphans -d"
    elif [ "$OPT" = "BUILD" ]; then
        mkdir -p 'draco/exp/large' 'draco/exp/medium' 'draco/exp/small' 'draco/exp/test' 'draco/log/time' 'draco/log/perf'
        run_cmd "docker-compose -f draco/docker-compose.draco.yml --compatibility up --build --remove-orphans -d"
    elif [ "$OPT" = "FORCE" ]; then
        mkdir -p 'draco/exp/large' 'draco/exp/medium' 'draco/exp/small' 'draco/exp/test' 'draco/log/time' 'draco/log/perf'
        run_cmd "docker-compose -f draco/docker-compose.draco.yml --compatibility up --build --no-cache --force-recreate --remove-orphans -d"
    elif [ "$OPT" = "STOP" ]; then
        run_cmd "docker-compose -f draco/docker-compose.draco.yml --compatibility down"
    elif [ "$OPT" = "CLEAN" ]; then
        run_cmd "rm -rf draco/exp/ draco/log/"
    elif [ "$OPT" = "LOGS" ]; then
        for cid in $(docker ps -q); 
        do
            echo "---------"; 
            echo "CONTAINER: ${cid}"; 
            docker logs --tail 10 $cid;
            echo;
        done
    fi
fi 
