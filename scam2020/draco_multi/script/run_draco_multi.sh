function run_draco_multi {
    EXPERIMENT_NAME="$1"

    GRAPHS_PATH=${DATA_PATH}/graphs/${EXPERIMENT_NAME}
    GRAPH_FILES=($(ls -SrA ${GRAPHS_PATH}))
    echo "GRAPH FILES: ${GRAPH_FILES[*]}"
    echo "FILES: ${#GRAPH_FILES[@]}"
    echo
    for idx in "${!GRAPH_FILES[@]}"; do
        mdg_name=${GRAPH_FILES[$idx]%.*}
        echo "${idx} > MDG NAME: ${mdg_name}"
        /usr/bin/time -v -o ${LOG_PATH}/${EXPERIMENT_NAME}/${mdg_name}.out ${BASE_PATH}/main --multi < ${GRAPHS_PATH}/${GRAPH_FILES[$idx]} > ${EXP_PATH}/${EXPERIMENT_NAME}/${mdg_name}.dot
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
                -n|--name)
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
            run_draco_multi $EXPERIMENT_NAME
        fi
    fi
}

run_script "$@"