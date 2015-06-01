.spinner-container {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 3000;
  .spinner-backdrop {
    width: 100%;
    height: 100%;
    background: black;
    position: absolute;
    .opacity(0.5)
  }
  button {
    width: 0px;
    height: 0px;
    .opacity(0);
  }
}