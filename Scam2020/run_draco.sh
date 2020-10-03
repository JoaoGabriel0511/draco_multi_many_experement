mdg_size=$1

echo "Welcome to SCM Performance Analyzer"
if [ "$mdg_size" = "" ]; then
    echo "  Choose the size of the mdg's dataset:"
    echo "    ./run.sh test, small, medium, large, all>"
    echo
else
    echo "MDG size choosen: $mdg_size"
    echo
    mkdir draco/dot
    for mdg in $(ls graphs/$mdg_size)
        do 
            mdg_name=${mdg%.*}
            echo "MDG NAME: $mdg_name"
            time -o time/${mdg_name}.draco --append ./main < graphs/$mdg_size/${mdg} > draco/dot/${mdg_name}.dot 2>> output.txt
        done
fi