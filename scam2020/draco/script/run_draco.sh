function run_draco {
    EXPERIMENT_NAME="$1"

    GRAPHS_PATH=${DATA_PATH}/graphs/${EXPERIMENT_NAME}
    GRAPH_FILES=($(ls -A ${GRAPHS_PATH}))
    echo "EXP NAME: ${EXPERIMENT_NAME}"
    echo "GRAPH FILES: ${GRAPH_FILES[*]}"
    echo "FILES: ${#GRAPH_FILES[@]}"
    echo
    for idx in "${!GRAPH_FILES[@]}"; do
        mdg_name=${GRAPH_FILES[$idx]%.*}
        echo "${idx} > MDG NAME: ${mdg_name}"
        /usr/bin/time -o ${LOG_PATH}/${EXPERIMENT_NAME}/${mdg_name}.out --append ${BASE_PATH}/main < ${GRAPHS_PATH}/${GRAPH_FILES[$idx]} > ${EXP_PATH}/${EXPERIMENT_NAME}/${mdg_name}.dot &
        PID=$!
        echo "Process ${PID} started."
        if ps -p $PID > /dev/null
        then
            echo "$PID is running..."
            sleep 1
        fi
        MEM=$(pmap ${PID} | tail -n 1 | awk '/[0-9]K/{print $2}' | sed 's/.$//')
        echo "$PID finished and used $MEM KB."
    done
}

function run_script {
    POSITIONAL=()
    EXPERIMENT_NAME=$1

    if [ "$1" = "" ]; then
        echo "  Experiment folder that contains the mdg's dataset:"
        echo "    ./run.sh -n/--name <experiment>"
        echo
    else
        EXPERIMENT_NAME=
        while [[ $# -gt 0 ]]
        do
            key="$1"

            case $key in
                -s|--size)
                EXPERIMENT_NAME=$2
                shift # past argument
                shift # past value
                ;;
                *)    # unknown option
                POSITIONAL+=("$1") # save it in an array for later
                shift # past argument
                ;;
            esac
        done

        set -- "${POSITIONAL[@]}" # restore positional parameters
        
        echo "EXP NAME = ${EXPERIMENT_NAME}"

        if [ "$EXPERIMENT_NAME" = "" ]; then
            echo "Please describe the experiment folder you want to run."
            exit 4
        else
            run_draco $EXPERIMENT_NAME
        fi
    fi
}

run_script "$@"