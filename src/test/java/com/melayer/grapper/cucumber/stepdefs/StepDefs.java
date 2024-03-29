package com.melayer.grapper.cucumber.stepdefs;

import com.melayer.grapper.GrapperApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = GrapperApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
