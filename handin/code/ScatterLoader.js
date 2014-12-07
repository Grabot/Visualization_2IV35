function drawScatterCars( chartData )
{

    var svg = dimple.newSvg("#scatterContainerCars", 1200, 600);
    var scatterChart = new dimple.chart(svg, chartData);
    scatterChart.setBounds(80, 30, 1040,530);
    scatterChart.addMeasureAxis("x", "companyCars");
    scatterChart.addMeasureAxis("y", "totalCars");
    scatterChart.addMeasureAxis("z", "inwoners");
    scatterChart.addSeries(["City", "companyCars", "totalCars"], dimple.plot.bubble);
    scatterChart.draw();

    return scatterChart;
}

function drawScatterMotorCycles( chartData )
{

    var svg = dimple.newSvg("#scatterContainerMotorCycles", 1200, 600);
    var scatterChart = new dimple.chart(svg, chartData);
    scatterChart.setBounds(80, 30, 1040,530);
    scatterChart.addMeasureAxis("x", "MotorCycles");
    scatterChart.addMeasureAxis("y", "totalCars");
    scatterChart.addMeasureAxis("z", "inwoners");
    scatterChart.addSeries(["City", "MotorCycles", "totalCars"], dimple.plot.bubble);
    scatterChart.draw();

    return scatterChart;
}

function drawScatterDensity( chartData )
{

    var svg = dimple.newSvg("#scatterContainerDensity", 1200, 600);
    var scatterChart = new dimple.chart(svg, chartData);
    scatterChart.setBounds(80, 30, 1040,530);
    scatterChart.addMeasureAxis("x", "singleHouseholds");
    scatterChart.addMeasureAxis("y", "totalHouseholds");
    scatterChart.addMeasureAxis("z", "inwoners");
    scatterChart.addSeries(["City", "singleHouseholds", "totalHouseholds"], dimple.plot.bubble);
    scatterChart.draw();

    return scatterChart;
}