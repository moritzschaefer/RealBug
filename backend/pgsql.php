<?php

					     "host=ec2-54-243-188-54.compute-1.amazonaws.com port=5482 dbname=d8253bs8koqicj user=rmtcunwfewenbn password=utQ0pLnQyZTJopK1UX4QSjCMwf sslmode=require options='--client_encoding=UTF8'"
    $dbconn = pg_connect("host=ec2-54-243-188-54.compute-1.amazonaws.com port=5482 dbname=d8253bs8koqicj user=rmtcunwfewenbn password=utQ0pLnQyZTJopK1UX4QSjCMwf sslmode=require options='--client_encoding=UTF8'") or die('Could not connect: ' . pg_last_error());
	$select = "Select * from bug";
	
	$result = pg_query($dbconn,$select);
	while($bug = pg_fetch_assoc($result)){
		echo $bug['description'];
	}
            
?>
