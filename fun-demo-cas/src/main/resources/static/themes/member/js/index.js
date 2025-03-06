function initNav(permissions) {
    const $nav = $("#nav");
    $nav.children().filter('li').remove();
    if (permissions) {
        const childArr = getChildArr(permissions, 0);
        childArr.forEach(function (childPermission) {
            $nav.append(createLi(permissions, childPermission))
        })
    }
}

function getChildArr(permissions, pid) {
    const childArr = [];
    permissions.forEach(function (permission) {
        if (permission.pid === pid) childArr.push(permission);
    });
    return childArr;
}

function createLi(permissions, permission) {
    const li = document.createElement('li'); // 生成一个 li 元素
    const cite = document.createElement('cite'); // 生成一个 cite 元素
    cite.innerText = permission.name;
    const childArr = getChildArr(permissions, permission.id);
    if (childArr.length > 0) {  // 有子菜单
        const a = document.createElement('a'); // 生成一个 a 元素
        a.setAttribute("href", "javascript:;");
        const iLift = document.createElement('i'); // 生成一个 i 元素
        iLift.setAttribute("class", "iconfont left-nav-li");
        iLift.setAttribute("lay-tips", permission.name);
        $(iLift).html(permission.icon);
        const iRight = document.createElement('i'); // 生成一个 i 元素
        iRight.setAttribute("class", "iconfont nav_right");
        iRight.setAttribute("lay-tips", permission.name);
        $(iRight).html("&#xe697;");
        a.appendChild(iLift);
        a.appendChild(cite);
        a.appendChild(iRight);
        const ul = document.createElement('ul'); // 生成一个 ul 元素
        ul.setAttribute("class", "sub-menu");
        childArr.forEach(function (childPermission) {
            ul.appendChild(createLi(permissions, childPermission))
        });
        li.appendChild(a);
        li.appendChild(ul);
    } else {    // 最低级菜单
        const a = document.createElement('a'); // 生成一个 a 元素
        a.setAttribute("onclick", "xadmin.add_tab('" + permission.name + "', '" + permission.operationUrl + "')");
        const iconfont = document.createElement('i'); // 生成一个 i 元素
        iconfont.setAttribute("class", "iconfont");
        iconfont.innerText = "&#xe6a7;";
        a.appendChild(cite);
        li.appendChild(a);
    }

    return li;
}
