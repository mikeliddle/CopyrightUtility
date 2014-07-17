#!/bin/bash

# NOTE: before running this on an account, verify you have added the SSH key to the account.  If not, it will not push.
# NOTE: in addition to adding the user to the users.txt document, you must also create a folder in gitHub for the user.
# NOTE: when you copy the files to the server, make sure they are in unix formattype "sed -i -e 's|\r||' your_script.sh"
#		replacing 'your_script.sh' with the file names.

# variables to use in the running of the service
run_mono='/usr/local/bin/mono'
run_git='/usr/bin/git'
repo_path_location='/opt/copyright_service/gitHub'
service_path_location='/opt/copyright_service'
message='added copyright headers'
copyrightServiceName='copyrightService.exe'
exclusion='remotes/origin/'
# end variables

function checkoutRunPush {
	branch=${branch#$exclusion}			
	
	cd $path/$repoName
	$run_git checkout $branch | grep remotes/origin
	
	echo $branch

	echo 'running copyrightService'
	cd $service_path_location
	$run_mono $service_path_location/$copyrightServiceName
	echo 'service finished'

	cd $path/$repoName
	$run_git commit -a -m "$message"
	$run_git push ssh://git@github.com/$user/$repoName
	echo 'git pushed'
}

while read line
do
	user=$line
	echo $user
	while read line
	do
		repoName=$line
		
		path=$repo_path_location/$user

		cd $path
		ls -al
		rm -rf $repoName
		
		echo "deleted $repoName"

		$run_git clone https://github.com/$user/$repoName
		echo "initial git pulled of $repoName"

		cd $path/$repoName

		#here we switch between branches to update all code...We hope.
		for branch in `git branch -a | grep remotes/origin`; do	
			checkoutRunPush			
		done	
	done < $2
done < $1