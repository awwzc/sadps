<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Chat</title>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-2.1.1.min.js"></script>
</head>
<body>
    <h2>File Upload</h2>
    Select file
    <input type="file" id="filename" />
    <br>
    <input type="button" value="Connect" onclick="connectChatServer()" />
    <br>
    <input type="button" value="Upload" onclick="sendFile()" />
    <script>
        var ws;

        function connectChatServer() {
            ws = new WebSocket("ws://localhost:8080/websocket8/websocket");

            ws.binaryType = "arraybuffer";
            ws.onopen = function() {
                alert("Connected.")
            };

            ws.onmessage = function(evt) {
                alert(evt.msg);
            };

            ws.onclose = function() {
                alert("Connection is closed...");
            };
            ws.onerror = function(e) {
                alert(e.msg);
            }

        }

        function sendFile() {
            var file = document.getElementById('filename').files[0];
            var reader = new FileReader();
            var rawData = new ArrayBuffer();            

            reader.loadend = function() {

            }
            reader.onload = function(e) {
                rawData = e.target.result;
                ws.send(rawData);
                alert("the File has been transferred.")
            }

            reader.readAsBinaryString(file);

        }


    </script>
</body>
</html>