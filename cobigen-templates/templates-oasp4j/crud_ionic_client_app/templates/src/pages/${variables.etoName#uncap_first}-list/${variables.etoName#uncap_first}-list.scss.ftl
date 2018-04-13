// http://ionicframework.com/docs/theming/


// Put style rules here that you want to apply to ${variables.etoName}.

.selected {
    background: color($colors, light, base);
    color : color($colors, dark, base);
}

.fab-md {
    margin-bottom: 10%!important;   
}

.fab-md:active {
    color: #fff;
    background-color: #488aff;
}


.ion-fab-list {
    margin: 80px 0!important;
}

// Changing the color of the button that closes the ion-fab-list
.fab-close-icon{
    background-color:  #3561b1!important;
}

// When the buttons are being shown
.fab-in-list.show{
    background-color: #488aff;
    color: #fff;
    transform: scale(1.3)!important;
    margin-bottom: 20%!important;
}

// When the buttons are spawned
.fab-md-in-list{
    color: #fff!important;
}

// Changing the size of the button icon
.fab ion-icon{
    font-size: 1.8rem!important;
}

a[disabled], button[disabled], [ion-button][disabled]{
    background-color: #e6e6e6!important;
    color: #b3b0b0!important;
}
