$(document).ready(function () {

    $('.edit').click(function () {
        $(this).hide();
        $(this).prev().hide();
        $(this).next().show();
        $(this).next().select();
    });


    $('.input_edit').blur(function () {
        if ($.trim(this.value) == '') {
            this.value = (this.defaultValue ? this.defaultValue : '');
        } else {
            $(this).prev().prev().html(this.value);
        }

        $(this).hide();
        $(this).prev().show();
        $(this).prev().prev().show();
        $('.form-post-submitting').submit();
    });

    $('input[.input_edit]').bind('keyup', function () {
        if ($.trim(this.value) == '') {
            this.value = (this.defaultValue ? this.defaultValue : '');
        } else {
            $(this).prev().prev().html(this.value);
        }

        $(this).hide();
        $(this).prev().show();
        $(this).prev().prev().show();
        $('.form-post-submitting').submit();
    });

    $('.input_edit').keypress(function (event) {
        if (event.keyCode == '13') {
            if ($.trim(this.value) == '') {
                this.value = (this.defaultValue ? this.defaultValue : '');
            } else
            {
                $(this).prev().prev().html(this.value);
            }

            $(this).hide();
            $(this).prev().show();
            $(this).prev().prev().show();
        }
    });

});