<?php
	//$json = '{"position":"1.200000,55.330000","description":"heyhou"}';
	$json = '{"position":"1.200000,55.330000","description":"heyhou"}';
	
	$object = json_decode($json, true);
	
	echo print_r($object, true);
	
	echo $object['position'];
	
	echo $json;
?>