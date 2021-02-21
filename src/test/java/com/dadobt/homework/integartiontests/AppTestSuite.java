package com.dadobt.homework.integartiontests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UsersControllerIT.class,
        MessageControllerIT.class,
        AuthenticateControllerIT.class
})
public class AppTestSuite {
}
