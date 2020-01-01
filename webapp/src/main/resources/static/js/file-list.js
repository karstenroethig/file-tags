
$( document ).ready( function() {

	$('[data-toggle="popover"]').popover({
		html: true
	});

	$('[data-toggle=popover]').on('click', function (e) {
		$('[data-toggle=popover]').not(this).popover('hide');
	});
	$('.popoverAddTag').on('shown.bs.popover', function () {
		$('#addTagInput').val('Test');
	});
	$('.popoverRename').on('shown.bs.popover', function () {
		$('#renameInput').val('Test');
	});
	$('.popoverMove').on('shown.bs.popover', function () {
		$('#moveInput').val('Test');
	});

	// remove tag modal: transfer the id to the modal form
	$( '#removeTagModal' ).on( 'show.bs.modal', function( event ) {
		var button = $( event.relatedTarget ); // Button that triggered the modal
		var fileId = button.data( 'file-id' ); // Extract info from data-* attributes
		var tagId = button.data( 'tag-id' ); // Extract info from data-* attributes

		// Update the modal's content.
		var modal = $( this );
		var link = modal.find( '.btn-danger' );
		var template = link.data( 'href-template' );
		link.attr( 'href', template.replace( '{fileId}', fileId ) );
		link.attr( 'href', template.replace( '{tagId}', tagId ) );
	});
});