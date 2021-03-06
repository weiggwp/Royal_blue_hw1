//test function to send and receive stuff front+backend when making a move

function sendRequest(gameID,target_x,target_y)
{
    var http = new XMLHttpRequest();
    var url = "/make_move";    //-> will be changed to another uri maybe action?=move
    var params = JSON.stringify({
        'name':'ying'
        });
    /*
    gameID: gameID,
        player: "user",
        start_x: null,
        start_y: null,
        end_x: target_x,
        end_y: target_y,
        status: undefined
     */
    //TODO: this method needs to be moved to Moves.js since we need prev_move coordinates
    //start_x and start_y need to be filled in to validate move

    http.open("POST", url, true);

    http.setRequestHeader("Content-type", "application/json; charset=utf-8");
    //http.setRequestHeader("Content-length", params.length);
    //http.setRequestHeader("Connection", "close");

    http.send(params);

    //will prob need to separate and make a more sophisticated function
    http.onload = function() {
        if (http.status != 200) { // analyze HTTP status of the response
            alert(`Error ${http.status}: ${http.statusText}`); // e.g. 404: Not Found
        } else { // show the result
            //alert(`Done, got ${http.response.length} bytes`); // responseText is the server
            alert(http.response.toString());
        }
    };
}