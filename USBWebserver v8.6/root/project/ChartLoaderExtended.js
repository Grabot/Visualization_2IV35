function drawChart( chartData )
{

	var svg = dimple.newSvg("#chartContainerExtended", 1200, 600);
    //var json = JSON.parse('${rlog}');
    
    var chart = new dimple.chart(svg, chartData);
    chart.setBounds(80, 30, 1040,530);
    var x = chart.addCategoryAxis("x",["Age", "City"]);
    x.addOrderRule(['00-04', '05-09', '10-14', '15-19', '20-24', '25-29', '30-34', '35-39', '40-44', '45-49', '50-54', '55-59', '60-65', '65-69', '70-74', '75-79', '80-84', '85-89', '90-94', '95-EO']);
    //x2.addOrderRule(['00-14', '15-24', '25-44', '45-64', '65-EO']);
    var y = chart.addMeasureAxis("y", "Pecentage");
    y.tickFormat = ',.1f';
    var bars = chart.addSeries("City", dimple.plot.bar, [x, y]);

    bars.barGap = 0.1;
    chart.addLegend(65, 10, 510, 20, "right" );

    chart.defaultColors = [
        new dimple.color("#30ff30"),
        new dimple.color("#3030ff")
    ];
    chart.draw(1000);

    return chart;
}