<?php
/**
 * Jaeger Tracer
 *
 * @author Janson
 * @create 2019-07-23
 */
namespace Asan\Trace;

use Jaeger\Config;

class JaegerTracer {
    /**
     * Jaeger agent host
     *
     * @var string
     */
    private static $host = "127.0.0.1";

    /**
     * Jaeger agent port
     *
     * @var int
     */
    private static $port = 6831;

    public static function getTracer($serverName) {
        $config = Config::getInstance();
        $config->gen128bit();

        $tracer = $config->initTrace($serverName, self::$host . ':' . self::$port);

        return $tracer;
    }

}
