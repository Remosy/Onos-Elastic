# COMS4200
## Inspiration
A traffic monitoring tool which used ELK: [Sweet security](https://github.com/TravisFSmith/SweetSecurity)

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/7DFg9Ez2sJE/0.jpg)](https://www.youtube.com/watch?v=7DFg9Ez2sJE)



:point_right: Test dataset: was found on http://statweb.stanford.edu/~sabatti/data.html

## Pre Process :one:
### Set environment on Ubuntu VM for ONOS
```bash
sudo apt-get install git
sudo apt-get install git-review
sudo apt-get install software-properties-common -y && \
sudo add-apt-repository ppa:webupd8team/java -y && \
sudo apt-get update && \
echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections && \
sudo apt-get install oracle-java8-installer oracle-java8-set-default -y
```
### Install ONOS (same for both mac and Ubuntu)
```bash
git clone https://gerrit.onosproject.org/onos
```
Add variables for default setting
```bash
vi ~/.bash_profile
```
Insert following lines to bash_profile file
```bash
export ONOS_ROOT=~/onos
source $ONOS_ROOT/tools/dev/bash_profile
```
Click: shift + ":" + "wq" to save and quit. And make sure you have installed "Buck" command, otherwise:
```bash
brew install buck
```

If use wget:
Go to page: https://www.elastic.co/start
![](https://github.com/Remosy/COMS4200/blob/master/Screen%20Shot%202017-10-02%20at%201.03.57%20am.png)
### Install Elastic
```bash
brew install Elastic
```
Ubuntu:
```bash
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.6.2.tar.gz
tar -xzf elasticsearch-5.6.2.tar.gz
./elasticsearch-5.6.2/bin/elasticsearch
```
### Install Kibana
```bash
brew install Kibana
```
Ubuntu:
```bash
wget https://artifacts.elastic.co/downloads/kibana/kibana-5.6.2-linux-x86_64.tar.gz
tar -xzf kibana-5.6.2-linux-x86_64.tar.gz
```
### Install Logstash
```bash
brew install Logstash
```
# Monitoring Mininet traffic Synchronously :two:
![alt text](https://image.slidesharecdn.com/bc47faae-18c7-45c9-b3f6-c400b4aff1d3-161215100509/95/software-architectures-week-3-microservicebased-architectures-41-638.jpg?cb=1482171534)
## Set ONOS as Mininet controller 
[Use 2 VM + ONOS on MacOSX](https://groups.google.com/a/onosproject.org/forum/#!topic/onos-discuss/5Z3OQFjLKF0) :mag:
* Create one VM with ONOS installed on Ubuntu with address localhost
* Create one VM with Mininet installed with address localhost
* Install ONOS locally on Mac OSX and create the cell to push to localhost
* :purple_heart: disable firewall of your laptop

## Set both ONOS on MacOSX and Ubuntu
Go inside of onos folder
```bash
cd onos
```
```bash
ok clean
```
Use ONOS to build a your own topology on localhost
```bash
buck build onos
```
If got the error:heavy_exclamation_mark: : Importing module random is forbidden: Change "buck" to "onos-buck".
[Click Here](https://groups.google.com/a/onosproject.org/d/topic/onos-dev/nMTghD3mLnQ?fromplusone=1) :mag:
Building process depends on your speed and firewall, even the network of onos-buck.
```bash
buck run onos-local
```
When the terminal stops poping new info texts(Like runing a server). 

Commandline(another cmd window): ```bash onos localhost ```

GUI: Open: http://localhost:8181/onos/ui/login.html The Default Username: onos; Password: rocks;
Localhost:127.0.0.1

#### Mininet ICMP(Ping): http://mininet.org/walkthrough/

#### ONOS show traffic https://wiki.onosproject.org/display/ONOS/Basic+ONOS+Tutorial#BasicONOSTutorial-Showalltraffic

## Connect Mininet with Logstash
Q:How to sychnously send Mininet log to Logistach 

A: with some latency, because of middle process.

On VM:

* Use self-defined Python([Pox](https://github.com/mininet/mininet/wiki/Introduction-to-Mininet#openflow-controllers)) file to do:
  * Test different settings(learning rules) of mininet (Via "Switch" 1...n cases)
  * Collect mininet output data(Ask tutor:certain function of traffic flow) and output under a same directory asynchronously

On Mac:

-- Locate the mininet outputs path of VirtualMachine from MacOSX: e.g: 
```bash 
ssh ubuntu@172.10.0.1 
//and locate a path
```

* Since the outputs of mininet will be always located at a same path(Don't need to change the path variable in logstash), then write the #SHELL file to run:
  * Countdown user determined timelength,and excute line "/usr/local/bin/logstash -f logstash.conf" to update data.
  * Refresh Kibana dashboard by exuting kyboard operation via calling another #SHELL file

## Connect Logistach with ElasticSearch and display on Kibana
Open elasticsearch and kibana separatly
```bash
/usr/local/bin/elasticsearch
```
```bash
/usr/local/bin/kibana
```
open link from kibana terminal window: http://localhost:5601
Assume get traffic data, and do [configuration](https://www.elastic.co/guide/en/logstash/current/configuration-file-structure.html) by createing a file named logstash.conf saved inside of logstash

[![IMAGE ALT TEXT HERE](https://github.com/Remosy/COMS4200/blob/master/Screen%20Shot%202017-08-27%20at%207.21.58%20pm.png)
```
input {
  file {
    path => "/Users/.../Desktop/test.csv"
    start_position =>"beginning"
  }
}
filter{
    csv{
        separator => ","
        columns => ["date","l_ipn","r_asn","f"]
    }

    mutate {convert => ["l_ipn","integer"]}
    mutate {convert => ["r_asn","integer"]}
    mutate {convert => ["f","integer"]}
}
output {
  elasticsearch {
     hosts => "localhost"
     index => "Traffic"
     document_type => "Monitoring traffic "
  }
  stdout{}
}

```
then run your configuration file to send data to elasticsearch
```bash
/usr/local/bin/logstash -f logstash.conf
```
Q: How to stop Logstash properly?

:ghost: :tada: :ghost:
tools' folders at:  ls /usr/local/Cellar

# What to analysis via Mininet and ONOS?

## Inspiration
[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/Q3ptlUWoAE8/0.jpg)](https://www.youtube.com/watch?v=Q3ptlUWoAE8)





