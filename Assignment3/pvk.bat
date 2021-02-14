javac -cp .;algs4.jar PrimVsKruskal.java
java -cp .;algs4.jar PrimVsKruskal Test_files_sparse\ewg_%1vertices_%2.txt > pvkout.txt

type Test_files_sparse\ewg_%1vertices_%2_output.txt
type pvkout.txt