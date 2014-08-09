15640 
=====
如何跑Experiment

0. cd to bin folder

run server

1. > rmiregistry


2. > java -cp ./ -Djava.security.policy=./experiment/rmi.policy experiment.Server

run client

3. > java -cp ./ -Djava.security.policy=./experiment/rmi.policy experiment.Client


stub.java 没有什么用，是我反汇编出来看rmic生成的stub代码


有用的链接

http://blog.csdn.net/wangjun88019014/article/details/4672520

http://docs.oracle.com/javase/tutorial/rmi/running.html

http://www.blogjava.net/boddi/archive/2006/10/11/74430.html
