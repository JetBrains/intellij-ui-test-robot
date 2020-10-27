// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
function show(x, y, width, height) {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", `/highlight?x=${x}&y=${y}&width=${width}&height=${height}`, true);
    xmlHttp.send(null);
}