function run_cmd {
    CMD=$1
    set -x
    ${CMD}
    set +x
}

function mkdirs {
    NAME=$1
    mkdir -p "experiments/draco_mono_lns/report/${NAME}"
    mkdir -p "experiments/draco_mono_lns/log/${NAME}"
    mkdir -p "experiments/draco_mono/report/${NAME}"
    mkdir -p "experiments/draco_mono/log/${NAME}"
    mkdir -p "experiments/draco_multi/report/${NAME}"
    mkdir -p "experiments/draco_multi/log/${NAME}"
    mkdir -p "experiments/draco_many/report/${NAME}"
    mkdir -p "experiments/draco_many/log/${NAME}"
    mkdir -p "experiments/bunch/report/${NAME}"
    mkdir -p "experiments/bunch/log/${NAME}"
    mkdir -p "experiments/hd_mono/report/${NAME}"
    mkdir -p "experiments/hd_mono/log/${NAME}"
}

echo "Welcome to SCM Performance Analyzer"
echo
if [ "$1" = "" ]; then
    echo "/run_all.sh [ACTIONS] [TOOLS] [ARGS]"
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
    echo "    --all [default]           Applies the actions above for draco (mono, multi and many) and bunch."
    echo "    -dmo/--draco-mono-lns     Applies the actions above only for draco mono objetive LNS."
    echo "    -dmo/--draco-mono         Applies the actions above only for draco mono objetive."
    echo "    -dmu/--draco-multi        Applies the actions above only for draco multiple objetives."
    echo "    -dma/--draco-many         Applies the actions above only for draco many objetives."
    echo "    -b/--bunch                Applies the actions above only for bunch."
    echo "    -hdmo/--hd-mono           Applies the actions above only for Heuristic Design Mono."
    echo
    echo " ARGS:"
    echo "    --detached        Starts experiment in detached mode."
    echo
else
    OPT= EXP=ALL NAME= DETACHED=NO
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
            NAME=$2
            shift # past argument
            shift # past value
            ;;
            -l|--logs)
            OPT=LOGS
            shift # past argument
            ;;
            -dmolns|--draco-mono-lns)
            EXP=DRACO_MONO_LNS
            shift # past argument
            ;;
            -dmo|--draco-mono)
            EXP=DRACO_MONO
            shift # past argument
            ;;
            -dmu|--draco-multi)
            EXP=DRACO_MULTI
            shift # past argument
            ;;
            -dma|--draco-many)
            EXP=DRACO_MANY
            shift # past argument
            ;;
            -b|--bunch)
            EXP=BUNCH
            shift # past argument
            ;;
            -hdmo|--hd-mono)
            EXP=HD_MONO
            shift # past argument
            ;;
            --all)
            EXP=ALL
            shift # past argument
            ;;
            --detached)
            DETACHED=YES
            shift # past argument
            ;;
            *)    # unknown option
            POSITIONAL+=("$1") # save it in an array for later
            shift # past argument
            ;;
        esac
    done

    set -- "${POSITIONAL[@]}" # restore positional parameters
        
    echo "OPT       = ${OPT}"
    echo "EXP       = ${EXP}"
    echo "DETACHED  = ${DETACHED}"

    EXTRA_ARGS=""
    if [ "$DETACHED" = "YES" ]; then
        EXTRA_ARGS="-d"
    fi

    if [ "$OPT" = "" ] && [ "$NAME" = "" ]; then
        echo "Please describe the script option."
        exit 4
    elif [ "$OPT" = "START" ]; then
        echo "NAME = ${NAME}"
        echo
        mkdirs "$NAME"
        if [ "$EXP" = "DRACO_MONO_LNS" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_mono_lns/docker-compose.yml --compatibility up --remove-orphans ${EXTRA_ARGS}
        fi
        
        if [ "$EXP" = "DRACO_MONO" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_mono/docker-compose.yml --compatibility up --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "DRACO_MULTI" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_multi/docker-compose.yml --compatibility up --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "DRACO_MANY" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_many/docker-compose.yml --compatibility up --remove-orphans ${EXTRA_ARGS}
        fi
        
        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f bunch/docker-compose.yml --compatibility up --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "HD_MONO" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f hd_mono/docker-compose.yml --compatibility up --remove-orphans ${EXTRA_ARGS}
        fi
    elif [ "$OPT" = "BUILD" ]; then
        echo "NAME = ${NAME}"
        echo
        mkdirs "$NAME"
        if [ "$EXP" = "DRACO_MONO_LNS" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_mono_lns/docker-compose.yml --compatibility up --build --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "DRACO_MONO" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_mono/docker-compose.yml --compatibility up --build --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "DRACO_MULTI" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_multi/docker-compose.yml --compatibility up --build --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "DRACO_MANY" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_many/docker-compose.yml --compatibility up --build --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f bunch/docker-compose.yml --compatibility up --build --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "HD_MONO" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f hd_mono/docker-compose.yml --compatibility up --build --remove-orphans ${EXTRA_ARGS}
        fi
    elif [ "$OPT" = "FORCE" ]; then
        echo "NAME = ${NAME}"
        echo
        mkdirs "$NAME"
        if [ "$EXP" = "DRACO_MONO_LNS" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_mono_lns/docker-compose.yml --compatibility up --build --no-cache --force-recreate --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "DRACO_MONO" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_mono/docker-compose.yml --compatibility up --build --no-cache --force-recreate --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "DRACO_MULTI" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_multi/docker-compose.yml --compatibility up --build --no-cache --force-recreate --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "DRACO_MANY" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_many/docker-compose.yml --compatibility up --build --no-cache --force-recreate --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f bunch/docker-compose.yml --compatibility up --build --no-cache --force-recreate --remove-orphans ${EXTRA_ARGS}
        fi

        if [ "$EXP" = "HD_MONO" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f hd_mono/docker-compose.yml --compatibility up --build --no-cache --force-recreate --remove-orphans ${EXTRA_ARGS}
        fi
    elif [ "$OPT" = "STOP" ]; then
        if [ "$EXP" = "DRACO_MONO_LNS" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_mono_lns/docker-compose.yml --compatibility down
        fi

        if [ "$EXP" = "DRACO_MONO" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_mono/docker-compose.yml --compatibility down
        fi

        if [ "$EXP" = "DRACO_MULTI" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_multi/docker-compose.yml --compatibility down
        fi

        if [ "$EXP" = "DRACO_MANY" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f draco_many/docker-compose.yml --compatibility down
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f bunch/docker-compose.yml --compatibility down
        fi

        if [ "$EXP" = "HD_MONO" ] || [ "$EXP" = "ALL" ]; then
            EXPERIMENT_NAME=$NAME docker-compose -f hd_mono/docker-compose.yml --compatibility down
        fi
    elif [ "$OPT" = "CLEAN" ]; then
        echo "NAME = ${NAME}"
        echo
        if [ "$EXP" = "ALL" ]; then
            run_cmd "rm -rf experiments"
        elif [ "$EXP" = "DRACO_MONO_LNS" ]; then
            run_cmd "rm -rf experiments/${NAME}/draco_mono_lns"
        elif [ "$EXP" = "DRACO_MONO" ]; then
            run_cmd "rm -rf experiments/${NAME}/draco_mono"
        elif [ "$EXP" = "DRACO_MULTI" ]; then
            run_cmd "rm -rf experiments/${NAME}/draco_multi"
        elif [ "$EXP" = "DRACO_MANY" ]; then
            run_cmd "rm -rf experiments/${NAME}/draco_many"
        elif [ "$EXP" = "BUNCH" ]; then
            run_cmd "rm -rf experiments/${NAME}/bunch"
        elif [ "$EXP" = "HD_MONO" ]; then
            run_cmd "rm -rf experiments/${NAME}/hd_mono"
        fi
    elif [ "$OPT" = "LOGS" ]; then
        if [ "$EXP" = "DRACO_MONO_LNS" ] || [ "$EXP" = "ALL" ]; then
            echo "---------"; 
            echo "DRACO MONO LNS IMAGE"; 
            docker logs -t --tail 50 scam2020_draco_mono_lns_experiment
            echo
        fi

        if [ "$EXP" = "DRACO_MONO" ] || [ "$EXP" = "ALL" ]; then
            echo "---------"; 
            echo "DRACO MONO IMAGE"; 
            docker logs -t --tail 50 scam2020_draco_mono_experiment
            echo
        fi

        if [ "$EXP" = "DRACO_MULTI" ] || [ "$EXP" = "ALL" ]; then
            echo "---------"; 
            echo "DRACO MULTI IMAGE"; 
            docker logs -t --tail 50 scam2020_draco_multi_experiment
            echo
        fi

        if [ "$EXP" = "DRACO_MANY" ] || [ "$EXP" = "ALL" ]; then
            echo "---------"; 
            echo "DRACO MANY IMAGE"; 
            docker logs -t --tail 50 scam2020_draco_many_experiment
            echo
        fi

        if [ "$EXP" = "BUNCH" ] || [ "$EXP" = "ALL" ]; then
            echo "---------"; 
            echo "BUNCH IMAGE"; 
            docker logs -t --tail 50 scam2020_bunch_experiment
            echo
        fi

        if [ "$EXP" = "HD_MONO" ] || [ "$EXP" = "ALL" ]; then
            echo "---------"; 
            echo "HD MONO IMAGE"; 
            docker logs -t --tail 50 scam2020_hd_mono_experiment
            echo
        fi
    fi
fi 
