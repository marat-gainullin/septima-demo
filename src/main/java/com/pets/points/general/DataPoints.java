package com.pets.points.general;

import com.septima.application.endpoint.SqlEntitiesDataEndPoint;

import javax.servlet.annotation.WebServlet;

@WebServlet(asyncSupported = true, urlPatterns = "/data/*")
public class DataPoints extends SqlEntitiesDataEndPoint {
}
