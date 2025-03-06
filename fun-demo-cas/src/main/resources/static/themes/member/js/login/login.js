function passwordWriting() {
    const $left_hand = $("#left_hand");
    const $right_hand = $("#right_hand");
    //得到焦点
    const passwordInput = document.getElementById('password');
    if (passwordInput === document.activeElement) {
        $left_hand.animate({
            left: "150",
            top: "-38px"
        }, {
            step: function () {
                if (parseInt($left_hand.css("left")) > 140) {
                    $left_hand.attr("class", "left_hand");
                }
            }
        });
        $right_hand.animate({
            right: "-64",
            top: "-38px"
        }, {
            step: function () {
                if (parseInt($right_hand.css("right")) > -70) {
                    $right_hand.attr("class", "right_hand");
                }
            }
        });
    }
}

function passwordWritten() {
    const $left_hand = $("#left_hand");
    const $right_hand = $("#right_hand");
    //失去焦点
    $left_hand.attr("class", "initial_left_hand");
    $left_hand.attr("style", "left:100px;top:-12px;");
    $right_hand.attr("class", "initial_right_hand");
    $right_hand.attr("style", "right:-112px;top:-12px");
}
