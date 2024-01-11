package com.example.gateway;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class GatewayServerApplicationTest
    extends TestCase
{

    public GatewayServerApplicationTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( GatewayServerApplicationTest.class );
    }

    public void testApp()
    {
        assertTrue( true );
    }
}
