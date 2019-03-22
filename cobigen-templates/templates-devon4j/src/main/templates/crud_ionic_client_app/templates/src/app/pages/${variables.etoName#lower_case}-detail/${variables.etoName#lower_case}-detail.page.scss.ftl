// http://ionicframework.com/docs/theming/

// Put style rules here that you want to apply to the form.
.formItem {
  margin-top: 4% !important;
  margin-right: 6%;
  margin-left: 6%;
}

.detail-message {
  text-align: center;
  font-size: 16px;
}

.form-content {
  padding-left: var(--form-content-pad-left);
  padding-right: var(--form-content-pad-left);
}

.buttonForm {
  height: var(--form-button-height);
  --padding-start: var(--form-button-pad-left);
  --padding-end: var(--form-button-pad-right);
}

.item.sc-ion-label-md-h,
.item .sc-ion-label-md-h {
  --color: var(--ion-color-medium);
}

ion-item {
  --padding-start: 0;
  --highlight-height: 0;
  border-bottom: 2px solid transparent;

  &.item-has-value {
    border-bottom: 2px solid var(--ion-color-secondary);
  }

  &.item-has-value:focus-within {
    border-bottom: 2px solid var(--ion-color-primary);
  }
}
