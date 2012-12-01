<?php

    $url=parse_url(getenv("CLEARDB_DATABASE_URL"));

    $server = $url["host"];
    $username = $url["user"];
    $password = $url["pass"];
    $db = substr($url["path"],1);

    mysql_connect($server, $username, $password);


            
    
    mysql_select_db($db);

    if(!mysql_query("CREATE TABLE \"test\" (\"ID\" \"INTEGER\")")) {
        echo mysql_error();
    }
    if(!mysql_query('INSERT INTO "test" ("ID") VALUES ("32")')) {
        echo mysql_error();
    }
    if($result = mysql_query('SELECT * FROM "test"') == false) {
        echo mysql_error();
    }
    echo var_dump(mysql_fetch_array($result));

    echo "asdf";
?>
