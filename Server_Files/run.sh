#!/bin/bash

# written by: Mike Liddle <mike@mikeliddle.com>
# 
# This script manages the git commands associated with an automated process.
# This script then calls the file copyrightService.exe and runs that. 
#
# NOTE: before running this on an account, verify you have added the SSH key to the account.  If not, it will not push.
# NOTE: in addition to adding the user to the users.txt document, you must also create a folder in gitHub for the user.
# NOTE: when you copy the files to the server, make sure they are in unix formattype "sed -i -e 's|\r||' your_script.sh"
#		replacing 'your_script.sh' with the file names.

# variables to use in the running of the service
declare -r RUN_GIT='/usr/bin/git'
declare -r REPO_PATH_LOCATION='/opt/copyright_service/gitHub'
declare -r SERVICE_PATH_LOCATION='CopyrightAdditionService/main.py'
# end variables

# $1 user
# $2 repo_name
# $3 path
function checkout_run_push {

	# variables for checkout_run_push
	local -r message='added copyright headers'
	local -r copyrightServiceName='copyrightService.exe'
	local -r exclusion='remotes/origin/'
	# end variables

	branch=${branch#$exclusion}			
	
	cd $3/$1
	$RUN_GIT checkout $branch | grep remotes/origin
	
	echo $branch

	echo 'running copyrightService'
	cd $SERVICE_PATH_LOCATION
	$SERVICE_PATH_LOCATION/$copyrightServiceName
	echo 'service finished'

	cd $3/$1
	$RUN_GIT commit -a -m "$message"
	$RUN_GIT push git@github.com:$2/$1
	echo 'git pushed'
}

# $1 repo_name
# $2 User
function delete_and_clone {

	local -r repo_name=$1	
	local -r path=$REPO_PATH_LOCATION/$2

	cd $path
	ls -al
	rm -rf $repo_name
	echo "deleted $repo_name"
				
	$RUN_GIT clone git@github.com:$2/$repo_name
	echo "initial git pulled of $repo_name"

	cd $path/$repo_name
	
	#here we switch between branches to update all code...We hope.
	for branch in `git branch -a | grep remotes/origin`; do	
		checkout_run_push $user $repo_name $path
	done
}

# $1 list of users
# $2 list of repositories
function main {
	while read line
	do
		local -r user=$line
		local -r echo $user
		while read line
		do
			delete_and_clone $line $user
		done < $2
	done < $1
}

main $1 $2