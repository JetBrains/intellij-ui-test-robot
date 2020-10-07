function show(x, y, width, height) {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", `/highlight?x=${x}&y=${y}&width=${width}&height=${height}`, true);
    xmlHttp.send(null);
}