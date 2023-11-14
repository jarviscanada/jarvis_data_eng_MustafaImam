# Introduction
The LCA (Linux Cluster Administration) team at Jarvis is responsible for overseeing a cluster of 10 Linux CentOS 7 servers. Their primary objective is to develop a Minimum Viable Product (MVP) designed to capture and track the hardware specifications of each server. Additionally, the MVP will monitor and log real-time usage data of the Linux servers, ranging from CPU to memory usage. This information is stored in a PostgreSQL Relational Database Management System (RDBMS) to aid in future resource allocation decisions, such as whether to scale up or down the server count. The data plays a crucial role in generating reports that guide the optimization of underutilized servers by adding more software and applications, as well as in the decision-making process for procuring new servers to alleviate the strain on overworked ones. The project is built using Bash scripts for data collection on hardware and server usage, Docker for setting up the PostgreSQL RDBMS, and Git/GitHub for managing the versions of the code, thereby ensuring a streamlined process for both data management and development.
 

# Quick Start

*eStart a PostgreSQL instance using `psql_docker.sh`:**
This scripts initializes a Docker container with the Postgres image. It allows us to run a Postgres instance where we can add, remove, and manage our databases and its tables. 
```
To create the PostgreSQL docker container
./scripts/psql_docker.sh create db_username db_password
# Example 
./scripts/psql_docker.sh create postgres password
```
**Create tables and use `ddl.sql` to define schema and populate the tables with sample data:**
The ddl.sql script defines the structure of the host_agent database, and the two tables host_info and host_usage. The former stores hardware data and the latter stores hardware usage for the aforementioned hardware.  
```
-- connect to the psql instance
psql -h localhost -U postgres -W
-- create the host_agent database 
postgres=# CREATE DATABASE host_agent; 
-- connect to the new database
postgres=# \c host_agent;
-- disconnect to the new host_agent database
postgres=# \q

-- Execute ddl.sql script 
psql -h localhost -U postgres -d host_agent -f sql/ddl.sql
```

** Insert a given hardware specification using `host_info.sh` OR insert one row of data into the host_info table:**
Run this script on each server to collect hardware specifications. This script only needs to be executed once during installation. Each row represents one hardware's specification. 
```
# Script usage 
bash scripts/host_info.sh psql_host psql_port db_name psql_user psql_password 
# Example 
bash scripts/host_info.sh localhost 5432 host_agent postgres password
```
**Collect hardware usage data using `host_usage.sh` OR insert multiple rows of ddata into the host_usage database table running `host_usage.sh` at different times:**
This script collects real-time server usage data and inserts it into the database. It makes sense to insert data using a crontab (Next step) as we may want to collect usage data overtime. 
```
# Script usage 
bash scripts/host_usage.sh psql_host psql_port db_name psql_user psql_password 
# Example 
bash scripts/host_usage.sh localhost 5432 host_agent postgres password
```
**Crontab implementation to automate collecting usage data:**
Using the below crontab we are executing the `host_usage.sh` every minute (interrvals can be changed)
```
# edit crontab jobs
bash> crontab -e

# add this to crontab
# make sure you are using the correct file location for your script
* * * * * bash /home/centos/dev/jrvs/bootcamp/linux_sql/host_agent/scripts/host_usage.sh localhost 5432 host_agent postgres password 

# list crontab jobs
crontab -l
```

**Validate your result from the psql instance**
```
psql -h localhost -U postgres -W
\l to list the dbs
\c host_agent
\dt to list he tables/relations
> SELECT * FROM host_usage;
\q to quit psql instance
```

# Implemenation
This is best described by bullet points:
- Initialized a PSQL environment using a Docker container using the psql_docker.sh script
- Initialized a database called host_agent and insert 2 tables called host_info and host_usage into it. The schema for each of the tables is defined in the ddl.sql file
- Inserted the hardware specific data into the host_info table using host_info.sh
- Inserted the hardware usage data into the host_usage table using host_usage.sh and the crontab (Crontab automatically ran host_usage every minute)


I began by establishing a Dockerized PostgreSQL environment using the psql_docker.sh script. Inside this Dockerized PostgreSQL environment, I created the host_agent database and defined its structure with the ddl.sql script, generating the host_info and host_usage tables. To automate harware and server usage data collection, I developed two Bash scripts: host_info.sh, responsible for gathering and inserting hardware specification data into the host_info table, and host_usage.sh, designed to continuously collect real-time server resource usage data, including CPU and memory, and insert it into the host_usage table. Finally, I configured crontab to automatically run the host_usage.sh script every minute, facilitating efficient data collection and regular reporting of server usage data.
## Architecture
![Linux SQL Project Architecture Diagram](../assets/diagram.png)

## Scripts
**Key scripts used in the project:**

**`psql_docker.sh`:** 

This scripts initializes a Docker container giving us a PSQL instance to create databases and tables within it. 
```
# script usage ./scripts/psql_docker.sh start|stop|create [db_username][db_password]
```

**`host_info.sh`:** 

This script inserts the specification of the hardware this script is running on. One entry per server 

```
# Script usage 
bash scripts/host_info.sh psql_host psql_port db_name psql_user psql_password 
# Example 
bash scripts/host_info.sh localhost 5432 host_agent postgres password
```

**`host_usage.sh`:** 

This scripts inserts real-time hardware usage data everytime it is run. If this script runs every minute then we will get real-time data for every one of the minutes this script is run on. 

```
# Script usage 
bash scripts/host_usage.sh psql_host psql_port db_name psql_user psql_password 
# Example 
bash scripts/host_usage.sh localhost 5432 host_agent postgres password
```

**`crontab`:** 

This runs the host_usage.sh script every minute giving us real-time data of the hardware's usage 

```
crontab -l

# validate your result from the psql instance
psql -h localhost -U postgres -W
\l to list the dbs
\c host_agent
\dt to list he tables/relations
> SELECT * FROM host_usage;
\q to quit psql instance
```


## Database Modeling
**There are two main tables in the host_agent database:**

**host_info Table:**
The host_info table contains hardware specifications for each host. The following is the host_info table schema:


| Column Name       | Data Type  | Constraints       | Description                                      |
|-------------------|------------|-------------------|--------------------------------------------------|
| id                | SERIAL     | PRIMARY KEY      | Unique auto-incremented identifier for each host|
| hostname          | VARCHAR    | NOT NULL, UNIQUE | Unique string representing the hostname of the host|
| cpu_number        | INT2       | NOT NULL          | Number of CPUs on the host                      |
| cpu_architecture  | VARCHAR    | NOT NULL          | String describing the CPU architecture         |
| cpu_model         | VARCHAR    | NOT NULL          | String specifying the CPU model                 |
| cpu_mhz           | FLOAT8     | NOT NULL          | CPU clock speed in megahertz                    |
| l2_cache          | INT4       | NOT NULL          | L2 cache size in bytes                          |
| timestamp         | TIMESTAMP  |                   | Timestamp indicating when the data was collected (nullable)|
| total_mem         | INT4       |                   | Total memory available on the host in bytes (nullable)|


**host_usage Table:**
The host_usage table records contains server usage information for each host. The following is the host_usage table schema:


| Column Name     | Data Type | Constraints                    | Description                                      |
|-----------------|-----------|--------------------------------|--------------------------------------------------|
| timestamp       | TIMESTAMP | NOT NULL                       | Timestamp indicating when host usage data was recorded|
| host_id         | SERIAL    | NOT NULL                       | Foreign key referencing `id` in `host_info` for host identification|
| memory_free     | INT4      | NOT NULL                       | Amount of free memory in bytes                   |
| cpu_idle        | INT2      | NOT NULL                       | Percentage of CPU idle time                     |
| cpu_kernel      | INT2      | NOT NULL                       | Percentage of CPU kernel time                   |
| disk_io         | INT4      | NOT NULL                       | Number of disk input/output operations          |
| disk_available  | INT4      | NOT NULL                       | Amount of available disk space in bytes        |

# Test
- Multiple tests done manually 
- Faulty test data was inserted 

# Deployment
In deploying the monitoring app, I set up Git repositories for source code management (using the Gitflow model), configured crontab for automated data collection, and provisioned the database using Docker

# Improvements
- Extend the project to allow analytics for the data collected 
- Implement alerts for under or over utilized resources 
- Better security for the whole infrastructure
