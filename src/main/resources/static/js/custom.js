$('.msg_history').ready(() => {
    var time = new Date();
    var url = '/api/sse/msghistory/' + time.toString();
    var es = new EventSource(url);
    es.addEventListener('message', function (msg) {
        var data = JSON.parse(msg.data);
        $(".msg_history").append("<div class=\"incoming_msg_img\"><img alt=\"Tushar\" src=\"https://ptetutorials.com/images/user-profile.png\"> </div><div class=\"received_msg\"> <div class=\"received_withd_msg\"> <p>" + data.text + "</p> <span class=\"time_date\">" + moment(new Date()).format('DD/MM/YYYY h:mm a') + "</span></div></div>");
        $(".msg_history").animate({scrollTop: $('.msg_history').prop("scrollHeight")}, 1000);
    });

})

$('.inbox_chat').ready(() => {
    let url = '/api/sse/recents';
    let es = new EventSource(url);
    es.addEventListener('message', (event) => {
            let eventhtml = $.parseHTML(event.data);
            let viewhtml = document.getElementById(eventhtml[0].id);
            if (viewhtml.innerHTML !== eventhtml[0].innerHTML) {
                viewhtml.innerHTML = eventhtml[0].innerHTML;
            }
        }
    );
})
$(document).ready(() => {
    $('.fixed-action-btn').floatingActionButton({
        toolbarEnabled: true
    });
    $(".msg_history").animate({scrollTop: $('.msg_history').prop("scrollHeight")}, 100);
    $("#send").click(() => {
        var req = {
            Text: $(".write_msg").val(),
            date: new Date()
        }
        postdata(req);
    });
    $(".write_msg").keypress((Event) => {
        var keycode = (Event.keyCode ? Event.keyCode : Event.which);
        if (keycode == '13') {
            var req = {
                Text: $(".write_msg").val(),
                date: new Date()
            }
            $(".msg_history").animate({scrollTop: $('.msg_history').prop("scrollHeight")}, 1000);
            postdata();
        }
    })
});
var postdata = () => {
    $.ajax({
        url: "/api/msg",
        method: "POST",
        data: {
            Text: $(".write_msg").val(),
            date: new Date(),
        },
        headers:
            {
                'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')
            },
        datatype: "json",
        success: (data, textstatus, jqhr) => {
            if (textstatus == 'success') {
                $(".msg_history").append("<div class=\"outgoing_msg\"> <div class=\"sent_msg\"> <p>" + $(".write_msg").val() + "</p> <span class=\"time_date\">" + moment(new Date()).format('DD/MM/YYYY h:mm a') + "</span></div></div>");
                $(".write_msg").val("");
            } else {
                $(".write_msg").val("don't know what happen");
            }

        }
    });
}
$('#action_button').ready(() => {
    $('#search_email').fadeOut(1);
    let isOn = false;
    $('#action_button').click(() => {
        $('#search_email').fadeToggle();
        if (isOn) {
            $('.chats').css({
                "filter": "blur(0px)"
            });
            isOn = false;
        } else {
            $('.chats').css({
                "filter": "blur(5px)"
            });
            isOn = true;
        }
        $('.email').keypress((Event) => {
            var keycode = (Event.keyCode ? Event.keyCode : Event.which);
            if (keycode == '13') {
                let email = $('.email').val();
                $('.email').val("");
                $.ajax({
                    url: "/api/addchat/" + email,
                    method: "GET",
                    headers:
                        {
                            'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content')
                        },
                    datatype: "json",
                    success: (data1, textstatus, jqhr) => {
                        if (textstatus == 'success') {
                            let count;
                            if ($('.chats')[0].lastElementChild == null) {
                                count = 1;
                            } else {
                                count = parseInt($('.chats')[0].lastElementChild.id) + 1;
                            }

                            $('.chats').append("" +
                                "<div id='" + count + "'>" +
                                "<a href='/chat/browse/" + data1.id + "'> <div class='chat_list hoverable'> <div class=\"chat_people\">\n" +
                                "                                <div class=\"chat_img\"><img alt=\"tushar\"\n" +
                                "                                                           src=\"https://ptetutorials.com/images/user-profile.png\"></div>\n" +
                                "                                <div class=\"chat_ib\">\n" +
                                "                                    <h5><span class=\"chat_date\"\n" +
                                "                                                                       >" + moment(data1.date).format('h:mm a') + "</span>\n" +
                                "                                    " + data1.usersidlist[data1.users[0]] + "</h5>\n" +
                                "                                     <p>" + "</p>\n" +
                                "                                </div>\n" +
                                "                            </div>" +
                                "</div>" +
                                "</a>" +
                                "</div>");
                            $('.email').val("");
                        } else {
                            $(".email").val("Can't find user");
                        }

                    }
                });


            }
        });
    });
})







