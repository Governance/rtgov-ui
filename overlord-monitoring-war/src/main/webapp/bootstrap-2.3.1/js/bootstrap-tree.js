$(document).on('click.tree.data-api', '.tree[data-toggle="tree"] span', function (e) {
  e.preventDefault();
  var children = $(this).parent('li.parent_li').find(' > ul > li');
  if (children.is(':visible')) {
    children.hide('fast');
    $(this).find(' > i').addClass('icon-plus-sign').removeClass('icon-minus-sign');
  }
  else {
    children.show('fast');
    $(this).find(' > i').addClass('icon-minus-sign').removeClass('icon-plus-sign');
  }
  e.stopPropagation();
});
