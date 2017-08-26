# COMS4200
A traffic monitoring tool which used ELK: [Sweet security](https://github.com/TravisFSmith/SweetSecurity)

## Pre Process :one:

### Install ONOS
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
### Install Elastic
```bash
brew install Elastic
```

### Install Kibana
```bash
brew install Kibana
```

### Install Logstash
```bash
brew install Logstash
```
# Monitoring Mininet traffic Synchronously :two:
![alt text](https://image.slidesharecdn.com/bc47faae-18c7-45c9-b3f6-c400b4aff1d3-161215100509/95/software-architectures-week-3-microservicebased-architectures-41-638.jpg?cb=1482171534)
## Set ONOS as Mininet controller 
Go inside of onos folder
```bash
cd onos
```
Use ONOS to build a your own topology on localhost
```bash
buck build onos
```
If got the error:heavy_exclamation_mark: : Importing module random is forbidden.
[Click Here](https://groups.google.com/a/onosproject.org/d/topic/onos-dev/nMTghD3mLnQ?fromplusone=1) :mag:
```bash
buck run onos-local
```
[Use 2 VM + ONOS on MacOSX](https://groups.google.com/a/onosproject.org/forum/#!topic/onos-discuss/5Z3OQFjLKF0) :mag:
* Create one VM with ONOS installed on Ubuntu with address localhost
* Create one VM with Mininet installed with address localhost
* Install ONOS locally on Mac OSX and create the cell to push to localhost
* :purple_heart: disable firewall of your laptop

## Connect Mininet with Logstash
Q:How to sychnously send Mininet log to Logistach
Q:Use TCP as input of Logistach?

## Connect Logistach with ElasticSearch
Open elasticsearch and kibana separatly
```bash
/usr/local/bin/elasticsearch
```
```bash
/usr/local/bin/kibana
```
open link from kibana terminal window: http://localhost:5601
Assume get traffic data, and do [configuration](https://www.elastic.co/guide/en/logstash/current/configuration-file-structure.html) by createing a file named logstash.conf saved inside of logstash
```
input {
  file {
    path => "/Users/.../Desktop/test.csv"
    type => "csv"
    start_position =>"beginning"
  }
}
filter{
    csv{
        separator => ","
        columns => ["date","l_ipn","r_asn","f"]
    }
    date {
      match => ["date", "yyyy-MM-dd"]
    }
    mutate {convert => ["l_ipn","integer"]}
    mutate {convert => ["r_asn","integer"]}
    mutate {convert => ["f","integer"]}
}
output {
  elasticsearch {
     host => "localhost"
     index => "Traffic"
     document_type => "Monitoring traffic "
  }
  stdout{}
}
```
then run your configuration file
```bash
/usr/local/bin/logstash -f logstash.conf
```
