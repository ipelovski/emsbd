class Fragment {
    constructor(elementId, controllerURL, search) {
        this.elementId = elementId;
        this.controllerURL = controllerURL;
        this.search = this._checkSearch(search);
        this.element = document.getElementById(this.elementId);
    }
    load(searchData) {
        let url = new URL(this.controllerURL);
        searchData = Object.assign({}, this._buildSearchData(), searchData);
        url.search = new URLSearchParams(searchData).toString();
        return fetch(url)
            .then(resp => resp.text())
            .then(html =>
                document.getElementById(this.elementId).innerHTML = html);
    }
    addParam(name, value) {
        this.search[name] = value;
        return this;
    }
    _checkSearch(search) {
        if (search === null || (typeof search !== 'function' && typeof search !== 'object')) {
            throw new Error('search should be a record or a function returning a record');
        }
        if (search === undefined) {
            search = {};
        }
        return search;
    }
    _buildSearchData() {
        if (typeof this.search === 'function') {
            return this.search();
        } else {
            let searchData = {};
            for (let key of Object.keys(this.search)) {
                searchData[key] = typeof this.search[key] === 'function' ?
                    this.search[key]() : this.search[key];
            }
            return searchData;
        }
    }
}
class ListFragment extends Fragment {
    constructor(mode, onSelect, ...args) {
        super(...args);
        this.mode = mode;
        this.onSelect = onSelect;
    }
    load(...args) {
        return super.load(...args)
            .then(() => this._init());
    }
    _init() {
        if (this.mode === 'singleSelect') {
            let buttons = this.element.querySelectorAll('ul input[type=button]');
            let eventListener = this._singleSelectClicked.bind(this);
            for (let button of buttons) {
                button.addEventListener('click', eventListener, false);
            }
        } else if (this.mode === 'multiSelect') {
            let multiSelectButton = this.element.querySelector('.multiSelect');
            multiSelectButton.addEventListener('click', this._multiSelectClicked.bind(this), false);
        }
    }
    _singleSelectClicked(e) {
        let objectId = e.target.getAttribute('data-object-id');
        this.onSelect(objectId);
        e.preventDefault();
    }
    _multiSelectClicked(e) {
        let checkboxes = this.element.querySelectorAll('ul input[type=checkbox]');
        let objectIds = [];
        for (let checkbox of checkboxes) {
            if (checkbox.checked) {
                objectIds.push(checkbox.value);
            }
        }
        this.onSelect(objectIds);
        e.preventDefault();
    }
}
export { Fragment, ListFragment };
