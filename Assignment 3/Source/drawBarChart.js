function drawBarChart( chartData )
{
    var svg = dimple.newSvg("#BarChart", 600, 400);
            
    var chart = new dimple.chart(svg, chartData);
    console.log(chartData);
    var x = chart.addCategoryAxis("x", "Disease");
    x.addOrderRule(['Low Temperature', 'High Temperature', 'Naseau', 'Lumbar Pain', 'Urine Pushing', 'Micturition pains', 'Burning of urethra', 'diagnosis inflammation of urinary bladder', 'diagnosis nephritis of renal pelvis origin']);
    var y = chart.addMeasureAxis("y", "Percentage of people");
    var s = chart.addSeries(null, dimple.plot.bar);
    chart.draw(500);

    return chart;
}