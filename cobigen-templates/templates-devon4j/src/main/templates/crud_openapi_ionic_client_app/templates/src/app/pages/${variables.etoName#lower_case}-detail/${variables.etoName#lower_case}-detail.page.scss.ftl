// http://ionicframework.com/docs/theming/


// Put style rules here that you want to apply to the form.
.formItem{
    margin-top: 4%!important;
    margin-right: 6%;
    margin-left: 6%;
}

.buttonForm{
    --height: auto;
    --padding-start:7%;
    --padding-end:7%;
}

.item.sc-ion-label-md-h, .item .sc-ion-label-md-h {
    --color: var(--ion-color-medium);
}

ion-item {
    --padding-start: 0;
    --highlight-color-valid: #3880ff;
    border-bottom: solid transparent;

    &.item-has-value {
        border-bottom: solid #10dc60;
    }

    &.item-has-value:focus-within {
        border-bottom: solid transparent;
    }
}
