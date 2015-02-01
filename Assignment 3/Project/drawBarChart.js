function drawBarChart( chartData )
{
    var svg = dimple.newSvg("#BarChart", 600, 400);
            
    var chart = new dimple.chart(svg, chartData);
    var x = chart.addCategoryAxis("x", "Disease");
    x.addOrderRule(['Naseau', 'Lumbar Pain', 'Urine Pushing', 'Micturition pains', 'Burning of urethra', 'diagnosis inflammation of urinary bladder', 'diagnosis nephritis of renal pelvis origin']);
    var y = chart.addMeasureAxis("y", "Percentage of people");
    var s = chart.addSeries("Diagnose", dimple.plot.bar);
    chart.draw(500);

    return chart;
}