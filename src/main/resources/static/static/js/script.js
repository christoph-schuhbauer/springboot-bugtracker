function alterIt(data){
    alert ("hello");
    console.log(data);
    data[1] = 10;
    console.log(data);
}


async function changeUrgencyChartToActiveAndWaitingTickets(val){


    console.log(val);
    if (val == 0){
        const  response = await fetch("http://localhost:8080/newChartData0");
        var dataso = await response.json();
    }else {
        const  response = await fetch("http://localhost:8080/newChartData1");
        var dataso = await response.json();
    }


    var datataList = [dataso["high"], dataso["low"], dataso["mid"]];

    console.log(datataList);

    let div = document.getElementById("chart2div");
    let canv = document.getElementById("myChart2");
    canv.remove();

    let canvsNew = document.createElement("canvas");
    canvsNew.setAttribute("id", "myChart2");

    div.append(canvsNew);

    //data[1] = 10;
    let ctx2 = document.getElementById("myChart2").getContext("2d");
    let myChart2 = new Chart(ctx2, {
        type: "pie",
        data:  {
            labels: [
                'high',
                'low',
                'mid'
            ],
            datasets: [{
                label: 'My First Dataset',
                data: datataList,
                backgroundColor: [
                    'rgb(255, 99, 132)',
                    'rgb(54, 162, 235)',
                    'rgb(255, 205, 86)',
                    'rgb(4, 150, 86)'
                ],
                hoverOffset: 4
            }]
        },
    });
}