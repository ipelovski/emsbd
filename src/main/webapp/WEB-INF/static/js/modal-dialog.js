import MicroModal from './micromodal.js';
MicroModal.init();
class Dialog {
    constructor(id) {
        this.id = id;
    }
    show() {
        MicroModal.show(this.id);
    }
    close() {
        MicroModal.close(this.id);
    }
    get title() {
        return this.titleElement.innerHTML;
    }
    set title(title) {
        this.titleElement.innerHTML = title;
    }
    get titleElement() {
        return document.getElementById(this.titleElementId);
    }
    get titleElementId() {
        return this.id + '-title';
    }
    get content() {
        return this.contentElement.innerHTML;
    }
    set content(content) {
        this.contentElement.innerHTML = content;
    }
    get contentElement() {
        return document.getElementById(this.contentElementId);
    }
    get contentElementId() {
        return this.id + '-content';
    }
}
export default Dialog;