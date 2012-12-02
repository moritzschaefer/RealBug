<?php
	//$json = '{"position":"1.200000,55.330000","description":"heyhou"}';
	$json = '{"position":"1.200000,55.330000","description":"heyhou"}';
	
	$object = json_decode($json, true);
	
	echo print_r($object, true);
	
	echo pg_escape_string(sprintf("INSERT INTO bug(description, lt, ln) VALUES ('%s', %s, %s)", $object['description'], 1.24342, 55.2344));
	
	echo $json;
?>