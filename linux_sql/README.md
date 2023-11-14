1.	Start a psql instance using psql_docker.sh
cd script: ./psql_docker.sh start (to start the docker script, script files are in the script directory)

2.	Create tables using ddl.sql
Using the SQL command CREATE TABLE table-name I created two tables ? host_info and host_usage inside the ddl.sql file

3.	Insert hardware specs data into the DB using host_info.sh
Using this query: insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, timestamp, total_mem) VALUES
('$hostname', 0, '', '', 0, 0, now(), 0) ON CONFLICT (hostname) DO NOTHING RETURNING id;"

4.	Insert hardware usage data into the DB using host_usage.sh
Using this query: insert_stmt="INSERT INTO host_usage(timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available) VALUES
('$timestamp', (SELECT id FROM host_info WHERE hostname='$hostname'), $memory_free, $cpu_idle, $cpu_kernel, $disk_io, $disk_available);"

5.	Crontab setup
Using crontab -e we can edit the crontab configuration, this will open a text editor in which I wrote ***** followed by my script path ensuring my script runs
every minute. After saving and exiting the text editor, new / modified cron job entries are stored in crontab?s configuration.

# Implemenation

I had to develop a product that could record the specifications of my hardware, as well as the data about resource usage, and store it in a database. I read
through all the relevant articles before beginning the project and organized my Github repository by making master, develop, release, and feature branches.  I set
up Docker and used a PSQL image to create a container. I used the docker start-stop commands to see whether my container was operating properly. I created a
psql_docker script because it was a bit tedious to type out docker commands every time. Thus, the container can be started or stopped simply by typing
./scriptname start/stop.  After that, I installed Postgres and linked it to the psql instance using psql -h HOST_NAME -p 5432 -U USER_NAME -d DB_NAME-p command.
I created a Host_agent database. After that, I created two tables in a ddl.sql file called host_info and host_usage to store my hardware specifications. Following
the execution of the SQL file, I created the host_info and host_usage scripts, which contained code for entering hardware specs and resource usage data into
databases, respectively. I started using crontab to deploy and automate the host_usage script after running the bash scripts. To edit the crontab configuration, I
typed crontab -e. This prompted a text editor, where I wrote ***** and then my script path. After saving and closing the text editor, the modified or new cron job
entries were saved in the crontab configuration.

## Architecture

The image for architecture is saved to the `assets` directory, please refer to that.

## Scripts

```bash
1. psql_docker.sh
This script simplifies the process of setting up and managing Postgresql db instances within a docker container.

2. host_info.sh
Connects to the PostgreSQL database server using the provided hostname, port, username, and password.
Gathers system information (such as hostname, CPU details, memory usage, etc.) from the host where the script is executed.
Insert this system information into the specified PostgreSQL database under the `host_agent` schema.
Usage: Used for monitoring and collecting data from the host and storing that data in a central DB.

3. host_usage.sh
Connects to the PostgreSQL database server using the provided hostname, port, username, and password.
Gathers system information (such as memory usage, CPU statistics, and disk usage) from the host where the script is executed.
Insert this system information into the specified PostgreSQL database under the `host_agent` schema.
Usage: Used for monitoring and collecting data from the host where it is executed and storing that data in a central DB. 

4. Crontab
Crontab is a time-based job scheduler in Unix-like operating systems. It allows users to schedule tasks and commands to run at specific times or intervals. 
Usage: Used for automating recurring tasks and scheduled job execution.

## Database Modeling
The image for database modeling is saved to the `assets` directory, please refer to that.

# Test

To execute the host_info table: ./host_info.sh "localhost" 5432 "host_agent" "postgres" "password"
To execute the host_usage table: bash ./host_usage.sh "localhost" 5432 "host_agent" "postgres" "password"
To connect to the host_agent db in Postgres: psql -h localhost -U postgres -d host_agent
To view the host_info table: SELECT * FROM host_info;
To view the host_usage table: SELECT * FROM host_usage;

As a result host info would give me the data about host specs whereas host usage will get me the updated information about the host_usage.

# Deployment

I started using crontab to deploy and automate the host_usage script after running the bash scripts. To edit the crontab configuration, I typed crontab -e. This
prompted a text editor, where I wrote ***** and then my script path. After saving and closing the text editor, the modified or new cron job entries were saved in
the crontab configuration.

# Improvements

When I first started using GitHub, I thought that changing the names of my commits would update my commits. So, I used the rebase function, passed the commit ID,
edited it, and used the git amend command. However, this resulted in an error saying that it was rejected. Afterward, I read through more documentation, changed
the temporal state of my uncommitted changes using git stash, and then used git fetch origin to fetch the most recent change from my remote repository and finally
merged it. As a result, my changes rolled back and the file that I deleted was also present in my folder so I had to delete the file, go through my scripts again
verify if there were any conflicts, modify it, and commit in my GitHub repo were also recursive. This led me to understand that:

1.	For each new feature I develop, I should make a new feature branch and merge it with the develop branch once everything functions without a hitch. This will
help to prevent minor hiccups.
2.	Since the goal of the project is to monitor resource usage we can use open-source alert tools like Zabbis and Nagios to send real-time alerts when predefined
thresholds are reached.
3.	I require additional practical experience with git concepts.


