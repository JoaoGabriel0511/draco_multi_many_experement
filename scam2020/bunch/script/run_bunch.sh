function run_bunch {
    mdg_size="$1"

    GRAPHS_PATH=${DATA_PATH}/graphs/${mdg_size}
    echo "MDG SIZE: ${mdg_size}"
    # get mdg per size, run draco and keep the time record of this experiment 
    echo "Files to test: $(ls ${GRAPHS_PATH} | wc -w)"
    echo
    for mdg in $(ls ${GRAPHS_PATH})
    do
        mdg_name=${mdg%.*}
        echo "MDG NAME: ${mdg_name}"
        # /usr/bin/time -o ${LOG_PATH}/time/${mdg_name}.out --append ${BASE_PATH}/main < ${GRAPHS_PATH}/${mdg} > ${EXP_PATH}/${mdg_size}/${mdg_name}.dot
        java -cp ${BASE_PATH}/.:Bunch-3.5.jar BunchAPITest ${GRAPHS_PATH}/${mdg}
    done
}

function run_exps {
    mdg_size="$1"
    mdg_types="test small medium large"

    if [ "$mdg_size" = "all" ]; then
        for size in ${mdg_types}; do
            run_bunch $size
        done
    else
        run_bunch $mdg_size
    fi
}


function run_script {
    POSITIONAL=()
    MDG_SIZE=$1

    if [ "$1" = "" ]; then
        echo "  Choose the size of the mdg's dataset:"
        echo "    ./run.sh -s/--size <test, small, medium, large, all>"
        echo
    else
        MDG_SIZE=
        while [[ $# -gt 0 ]]
        do
            key="$1"

            case $key in
                -s|--size)
                MDG_SIZE=$2
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
        
        echo "MDG_OPTION = ${MDG_SIZE}"

        if [ "$MDG_SIZE" = "" ]; then
            echo "Please describe the mdg type you want to test."
            exit 4
        else
            run_exps $MDG_SIZE
        fi
    fi
}

run_script "$@"