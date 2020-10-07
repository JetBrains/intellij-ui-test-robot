class XpathEditor {
    static get _goodAttributes() {
        return ['class', 'text', 'name', 'myaction', 'accessiblename']
    }

    constructor(element) {
        this.element = element
        this.editor = element.getElementsByTagName("xpathEditor")[0]
        this.xpathTextField = this.editor.getElementsByTagName("input")[0]
        this.xpathLabel = this.editor.getElementsByTagName("label")[0]
        this.editor.setAttribute("class", this.editor.getAttribute("class").replace("hidden", ""))
    }

    generatePath() {
        this.xpathTextField.value = `//${this.element.tagName.toLowerCase()}${this._formatAttributes()}`;
        this.checkXpath()
    }

    checkXpath() {
        try {
            const xpath = this.xpathTextField.value
            const elementsFoundByXpath = this._countElementByXpath(xpath)

            if (elementsFoundByXpath !== 1) {
                this.xpathTextField.setAttribute("class", "badXpath")
                this.xpathLabel.textContent = `${elementsFoundByXpath} matches`
            } else {
                const result = document.evaluate(xpath, document, null, XPathResult.ANY_TYPE, null).iterateNext();
                if (result === this.element) {
                    this.xpathTextField.setAttribute("class", "goodXpath")
                    this.xpathLabel.textContent = `1 match!`
                } else {
                    this.xpathTextField.setAttribute("class", "badXpath")
                    this.xpathLabel.textContent = `matched wrong element`
                }
            }
        } catch (e) {
            this.xpathTextField.setAttribute("class", "invalidXpath")
            this.xpathLabel.textContent = `invalid xpath`
        }
    }

    _countElementByXpath(xpath) {
        return document.evaluate(`count(${xpath})`, document, null, XPathResult.ANY_TYPE, null).numberValue;
    }

    _formatAttributes() {
        const attributes = this.element.attributes

        if (attributes.length === 0) {
            return ""
        }
        if (attributes.length === 1) {
            return `[@${attributes[0].name}='${attributes[0].value}']`
        }
        let result = ""
        for (let i = 0; i < attributes.length; i++) {
            if (XpathEditor._goodAttributes.includes(attributes[i].name) && attributes[i].value.length > 1) {
                if (result.length === 0) {
                    result = `[@${attributes[i].name}='${attributes[i].value}'`
                } else {
                    result = `${result} and @${attributes[i].name}='${attributes[i].value}'`
                }
            }
        }
        if (result.length > 0) {
            result = `${result}]`
        }
        return result
    }
}