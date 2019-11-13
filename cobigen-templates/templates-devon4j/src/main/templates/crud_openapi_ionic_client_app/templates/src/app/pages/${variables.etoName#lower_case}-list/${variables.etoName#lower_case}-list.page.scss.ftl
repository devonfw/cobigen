// http://ionicframework.com/docs/theming/

// Put style rules here that you want to apply to ${variables.etoName}.

.header-attributes {
  background-color: #ddd;
  padding-left: 7%;
  padding-right: 9%;
}

.header-span {
  background-color: #ddd;
}

.header-grid {
  background-color: #dddddd;
}

.grid-margin {
  margin: 13px 8px 13px 0;
}

.crop {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.item-inner {
  --padding-right: 0% !important;
}

.selected {
  --background: var(--color-light);
  --color: var(--colors-dark);
}

.fab-md {
  margin-bottom: 10% !important;
}

.fab-md:active {
  color: #fff;
  background-color: var(--color-primary);
}

.ion-fab-list {
  margin: 80px 0 !important;
}

// Changing the color of the button that closes the ion-fab-list
.fab-close-icon {
  background-color: #3561b1 !important;
}

// When the buttons are being shown
.fab-in-list.show {
  background-color: var(--color-primary);
  color: #fff;
  transform: scale(1.3) !important;
  margin-bottom: 20% !important;
}

// When the buttons are spawned
.fab-md-in-list {
  color: #fff !important;
}

// Changing the size of the button icon
.fab ion-icon {
  font-size: 1.8rem !important;
}

a[disabled],
button[disabled],
[ion-button][disabled] {
  background-color: #e6e6e6 !important;
  color: #b3b0b0 !important;
}

ion-fab-list ion-fab-button {
  height: 52px;
  width: 52px;

  ion-icon {
    height: 24px;
    width: 24px;
  }
}

ion-fab ion-fab-button.fab-button-close-active {
  --ion-color-base: #3561b1 !important;
}

ion-item-option ion-icon {
  height: 24px;
  width: 24px;
}
