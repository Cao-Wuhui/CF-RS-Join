# An Efficient Candidate-Free R-S Set Similarity Join Algorithm with the Filter-Verification Tree and MapReduce

This repository contains the source code and datasets for the paper *An Efficient Candidate-Free R-S Set Similarity Join Algorithm with the Filter-Verification Tree and MapReduce* submitted to PVLDB 2026.

## Overview

This repository provides the Java implementation of two algorithms for fast set similarity RS-Joins:

- **CF-RS-Join/FVT**: A fast algorithm using Filter-Verification tree (FVT) for set similarity RS-joins.
- **MR-CF-RS-Join/FVT**: Distributed CF-RS-Join/FVT, which exploits the MapReduce for further speeding up the CF-RS-Join/FVT computation respectively. 

## Dependencies

- OpenJDK 1.8.0
- Apache Hadoop 2.7
- Apache Maven (for building the project)

## Datasets

Apart from the Facebook dataset, due to GitHub's file size upload limitations (50 MB), we are unable to include the datasets in this repository. Please download the datasets required for the experiment from the following website.

- Dblp: [https://www2.cs.sfu.ca/~jnwang/data/adapt/querylog.format(data+query).tar.gz](https://www2.cs.sfu.ca/~jnwang/data/adapt/querylog.format(data+query).tar.gz)
- Kosarak: [http://fimi.uantwerpen.be/data/](http://fimi.uantwerpen.be/data/)
- Enron: [https://www.cs.cmu.edu/~./enron/](https://www.cs.cmu.edu/~./enron/)
- LiveJ, Querylog, and Orkut: [http://ssjoin.dbresearch.uni-salzburg.at/datasets.html](http://ssjoin.dbresearch.uni-salzburg.at/datasets.html)
