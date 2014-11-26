function drawScatter( chartData )
{

    var svg = dimple.newSvg("#scatterContainer", 600, 400);
    var scatterChart = new dimple.chart(svg, chartData);
    scatterChart.addMeasureAxis("x", "companyCars");
    scatterChart.addMeasureAxis("y", "totalCars");
    scatterChart.addMeasureAxis("z", "inwoners");
    scatterChart.addSeries(["City", "companyCars", "totalCars"], dimple.plot.bubble);
    scatterChart.draw();

    return scatterChart;
}