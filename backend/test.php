<?php
	echo ".";
	$data = parse_str(file_get_contents("php://input"),$post_vars);
	
	print_r($post_vars);
	echo $data . "=)";
?>