$("#searchForm").submit(async function (event) {
    event.preventDefault();
    const q = $('#searchInputID').val();
    if (q == "") {
        return;
    }

    var r = await fetch('/Reviews/Search/' + q);
    var filterList = await r.json();
    console.log(filterList);
    const template = $('#template').html();
    let results = '';
    for (var item in filterList) {
        let row = template;
        for (var key in filterList[item]) {
            row = row.replaceAll('{' + key + '}', filterList[item][key]);
        }
        results += row;
    }
    $("#tableBody").html(results);

}); 