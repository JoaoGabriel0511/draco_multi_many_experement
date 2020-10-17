function run_cmd {
    CMD=$1
    set -x
    ${CMD}
    set +x
}

function mkdirs {
    mkdir -p 'draco/exp/large' 'draco/exp/medium' 'draco/exp/small' 'draco/exp/test'
    mkdir -p 'draco/log/large' 'draco/log/medium' 'draco/log/small' 'draco/log/test'
    mkdir -p 'bunch/exp/large' 'bunch/exp/medium' 'bunch/exp/small' 'bunch/exp/test'
    mkdir -p 'bunch/log/large' 'bunch/log/medium' 'bunch/log/small' 'bunch/log/test'
}

echo "Welcome to SCM Performance Analyzer"
echo
if [ "$1" = "" ]; then
    echo "/run_all.sh [ACTIONS] [EXP]"
    echo
    echo " ACTIONS:"
    echo "    -s/--start        Start [EXP]."
    echo "    -p/--build        Build all the images again (with cache) and start [EXP]."
    echo "    -f/--force-build  Build all the images again (no cache) and start [EXP]."
    echo "    -p/--stop         Stop [EXP]."
    echo "    -c/--clean        [WARNING] Remove all experiment files and log data from [EXP]."
    echo "    -c/--logs         Show the last 10 lines from all containers up from [EXP]."
    echo
    echo " EXP:"
    echo "    --all [default]   Applies the actions above for draco and bunch."
    echo "    -d/--draco        Applies the actions above only for draco."
    echo "    -b/--bunch        Applies the actions above only for bunch."
    echo
else
    OPT= EXP=ALL
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
            -d|--draco)
            EXP=DRACO
            shift # past argument
            ;;
            -b|--bunch)
            EXP=BUNCH
            shift # past argument
            ;;
            --all)
            EXP=ALL
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
    echo "EXP = ${EXP}"

    if [ "$OPT" = "" ]; then
        echo "Please describe script option."
        exit 4
    elif [ "$OPT" = "START" ]; then
        mkdirs
        if [ "$EXP" = "DRACO" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f draco/docker-compose.yml --compatibility up --remove-orphans -d"
        fi
        
        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f bunch/docker-compose.yml --compatibility up --remove-orphans -d"
        fi
    elif [ "$OPT" = "BUILD" ]; then
        mkdirs
        if [ "$EXP" = "DRACO" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f draco/docker-compose.yml --compatibility up --build --remove-orphans -d"
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f bunch/docker-compose.yml --compatibility up --build --remove-orphans -d"
        fi
    elif [ "$OPT" = "FORCE" ]; then
        mkdirs
        if [ "$EXP" = "DRACO" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f draco/docker-compose.yml --compatibility up --build --no-cache --force-recreate --remove-orphans -d"
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f bunch/docker-compose.yml --compatibility up --build --no-cache --force-recreate --remove-orphans -d"
        fi
    elif [ "$OPT" = "STOP" ]; then
        if [ "$EXP" = "DRACO" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f draco/docker-compose.yml --compatibility down"
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f bunch/docker-compose.yml --compatibility down"
        fi
    elif [ "$OPT" = "CLEAN" ]; then
        if [ "$EXP" = "DRACO" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "rm -rf draco/exp/ draco/log/"
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "rm -rf bunch/exp/ bunch/log/"
        fi
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
