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