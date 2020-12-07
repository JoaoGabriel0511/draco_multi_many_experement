function run_cmd {
    CMD=$1
    set -x
    ${CMD}
    set +x
}

function mkdirs {
    NAME=$1
    mkdir -p "experiments/${NAME}/draco/report"
    mkdir -p "experiments/${NAME}/draco/log"
    mkdir -p "experiments/${NAME}/bunch/report"
    mkdir -p "experiments/${NAME}/bunch/log"
}

echo "Welcome to SCM Performance Analyzer"
echo
if [ "$1" = "" ]; then
    echo "/run_all.sh [ACTIONS] [TOOLS]"
    echo
    echo " ACTIONS: [REQUIRED]"
    echo "    -s/--start experiment_1       Start experiment_1 for [TOOLS]."
    echo "    -p/--build experiment_1       Build all the images (with cache) and start experiment_1 for [TOOLS]."
    echo "    -f/--force-build experiment_1 Build all the images (no cache) and start experiment_1 for [TOOLS]."
    echo "    --stop                        Stop [TOOLS]."
    echo "    -c/--clean experiment_1       [WARNING] Remove all experiment_1 reports and logs from [TOOLS]."
    echo "    -l/--logs                     Show the last 10 lines from all containers up from [TOOLS]."
    echo
    echo " EXP:"
    echo "    --all [default]   Applies the actions above for draco and bunch."
    echo "    -d/--draco        Applies the actions above only for draco."
    echo "    -b/--bunch        Applies the actions above only for bunch."
    echo
else
    OPT= EXP=ALL NAME=
    while [[ $# -gt 0 ]]
    do
        key="$1"
        case $key in
            -s|--start)
            OPT=START
            NAME=$2
            shift # past argument
            shift # past value
            ;;
            -b|--build)
            OPT=BUILD
            NAME=$2
            shift # past argument
            shift # past value
            ;;
            -f|--force-build)
            OPT=FORCE
            NAME=$2
            shift # past argument
            shift # past value
            ;;
            --stop)
            OPT=STOP
            shift # past argument
            ;;
            -c|--clean)
            OPT=CLEAN
            OPT=$2
            shift # past argument
            shift # past value
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
        
    echo "OPT  = ${OPT}"
    echo "EXP  = ${EXP}"

    if [ "$OPT" = "" ] && [ "$NAME" = "" ]; then
        echo "Please describe the script option."
        exit 4
    elif [ "$OPT" = "START" ]; then
        echo "NAME = ${NAME}"
        export EXPERIMENT_NAME=${NAME}
        echo
        mkdirs "$NAME"
        if [ "$EXP" = "DRACO" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f draco/docker-compose.yml --compatibility up --remove-orphans -d"
        fi
        
        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f bunch/docker-compose.yml --compatibility up --remove-orphans -d"
        fi
    elif [ "$OPT" = "BUILD" ]; then
        echo "NAME = ${NAME}"
        echo
        mkdirs "$NAME"
        if [ "$EXP" = "DRACO" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f draco/docker-compose.yml --compatibility up --build --remove-orphans -d"
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "docker-compose -f bunch/docker-compose.yml --compatibility up --build --remove-orphans -d"
        fi
    elif [ "$OPT" = "FORCE" ]; then
        echo "NAME = ${NAME}"
        echo
        mkdirs "$NAME"
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
        echo "NAME = ${NAME}"
        echo
        if [ "$EXP" = "DRACO" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "rm -rf experiments/${NAME}/draco/"
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            run_cmd "rm -rf experiments/${NAME}/bunch/"
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
