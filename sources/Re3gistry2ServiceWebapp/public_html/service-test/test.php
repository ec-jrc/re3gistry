<?php

$testError = (isset($_REQUEST['testError']) ? $_REQUEST['testError'] : NULL);
$testRewrite = (isset($_REQUEST['testRewrite']) ? $_REQUEST['testRewrite'] : NULL);
$lang = (isset($_REQUEST['lang']) ? $_REQUEST['lang'] : NULL);
$format = (isset($_REQUEST['format']) ? $_REQUEST['format'] : NULL);
$uri = (isset($_REQUEST['uri']) ? $_REQUEST['uri'] : NULL);

if ($testError) {

    if ($testError === '404') {
        header("HTTP/1.1 404 Not Found");
        header('Content-Type: application/json');
        echo '{"error":{"code": "404","description-code":"not-found","description": "Element not found"}}';
    } else if ($testError === '406') {
        header("HTTP/1.1 406 Not Acceptable");
        header('Content-Type: application/json');
        echo '{"error":{"code": "406","description-code":"unknown-format","description": "The requested media type is not supported"}}';
    } else if ($testError === '500') {
        header("HTTP/1.1 500 Internal Server Error");
        header('Content-Type: application/json');
        echo '{"error":{"code": "500","description-code":"internal-server-error","description": "The server had an internal error"}}';
    }
    
} else if($testRewrite){

    echo "<h1>-- Test Rewrite --</h1>";
    echo "lang: " . $_REQUEST['lang'] . "<br/>";
    echo "uri: " . $_REQUEST['uri'] . "<br/>";
    echo "format: " . $_REQUEST['format'] . "<br/>";
    
}else{
    
    header("HTTP/1.1 200 Ok");
    header('Content-Type: application/json');
    echo file_get_contents('test.json');
    
}