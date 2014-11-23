function drawChart( chartData )
{

	var svg = dimple.newSvg("#chartContainer", 800,400);
    //var json = JSON.parse('${rlog}');
    
    var chart = new dimple.chart(svg, chartData);
    chart.setBounds(80, 30, 720,330);
    var x = chart.addCategoryAxis("x",["Age"]);
    x.addOrderRule(['00-04', '05-09', '10-14', '15-19', '20-24', '25-29', '30-34', '35-39', '40-44', '45-49', '50-54', '55-59', '60-65', '65-69', '70-74', '75-79', '80-84', '85-89', '90-94', '95-EO']);
    var y1 = chart.addMeasureAxis("y", "Pecentage");
    y1.tickFormat = ',.1f';
    var bars = chart.addSeries("Metric", dimple.plot.bar, [x, y1]);
    bars.barGap = 0.5;
    //chart.addLegend(65, 10, 510, 20, "right");
    chart.draw(1000);

    return chart;
}