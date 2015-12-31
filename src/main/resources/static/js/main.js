
var app = {};

app.Comment = Backbone.Model.extend({
  defaults: {
    email: "",
    createdAt: "",
    modifiedAt: "",
    content: ""
  },
  validate: (function () {
    var regexEmail = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
    return function () {
      if (!regexEmail.test(this.get("email"))) {
        return "Invalid Email";
      }
    };
  }()),
  pickBgClass: (function () {
    var colorClasses = [ "bg-green", "bg-blue", "bg-orange", "bg-red" ];
    function hashSome (str) {
      var s = str.trim();
      var val = 0;
      for (i = 0; i < 4; i++) {
        val = val * 31 + s.charCodeAt(i);
      }
      return val;
    }
    return function () {
      var hashed = hashSome(this.get("email"));
      return colorClasses[hashed % 4];
    }
  }()),
  parseOptions: {
    weekday: "long", year: "numeric", month: "short",
    day: "numeric", hour: "2-digit", minute: "2-digit"
  },
  parse: function (res) {
    res.contentDisplay = res.content.replace(/\n/g, "<br>");
    res.createdAtDisplay = (new Date(res.createdAt))
                            .toLocaleTimeString("ko-KR", this.parseOptions);
    res.modifiedAtDisplay = (new Date(res.modifiedAt))
                            .toLocaleTimeString("ko-KR", this.parseOptions);
    return res;
  }
});

app.CommentCollection = Backbone.Collection.extend({
  url: "/api/visitorbooks",
  model: app.Comment
});

app.CommentView = Backbone.View.extend({
  tagName: "article",
  className: "comment box",
  initialize: function () {
    this.listenTo(this.model, "change", this.render);
    this.listenTo(this.model, "remove", this.remove);
  },
  events: {
    "click #btn-modify": "modify"
  },
  template: _.template($("#comment-template").html()),
  render: function () {
    this.$el.html(this.template(this.model.attributes));
    var picked = this.model.pickBgClass();
    this.$(".circle").addClass(picked);
    return this;
  },
  modify: function () {
    this.trigger("modify", this.model);
  }
});

app.VisitorBookView = Backbone.View.extend({
  el: ".page-main",
  initialize: function () {
    this.comments = new app.CommentCollection();
    
    this.$email = this.$("#email");
    this.$passwd = this.$("#passwd");
    this.$content = this.$("#content");
    this.$editPasswd = this.$("#edit-passwd");
    this.$editContent = this.$("#edit-content");
    this.$commentList = this.$(".comment-list");
    this.$commentEdit = this.$(".comment-edit");
    
    this.listenTo(this.comments, 'add', this.add);
    this.listenTo(this.comments, 'reset', this.addAll);
    this.comments.fetch({ reset: true });
  },
  events: {
    "click #btn-save": "save",
    "click #btn-edit-save": "modify",
    "click #btn-edit-cancel": "closeModifyForm"
  },
  add: function (comment) {
    var view = new app.CommentView({ model: comment });
    this.$commentList.prepend(view.render().el);
    this.listenTo(view, "modify", this.showModifyForm);
  },
  addAll: function () {
    this.$commentList.html('');
    this.comments.each(this.add, this);
  },
  save: function () {
    var comment = new app.Comment({
      email: this.$email.val(),
      passwd: this.$passwd.val(),
      content: this.$content.val()
    });
    
    if (comment.isValid()) {
      this.comments.create(comment, { 
        wait: true,
        error: function (model, response) {
          if (response.status == 400) {
            self.handleInputError(self.$email);
          }
        }
      });
      this.$email.val("");
      this.$passwd.val("");
      this.$content.val("");
    } else {
      this.handleInputError(this.$email);
    }
  },
  modify: function () {
    var self = this;
    this.modelToEdit.set("passwd", this.$editPasswd.val());
    this.modelToEdit.set("content", this.$editContent.val());
    this.modelToEdit.save(this.modelToEdit.attributes, { 
      wait: true,
      success: function (model) {
        self.comments.remove(model)
        self.comments.add(model);
        self.closeModifyForm();
      },
      error: function (model, response) {
        if (response.status == 404) {
          self.handleInputError(self.$editPasswd);
        } else {
          self.showUnknownError();
        }
      }
    });
  },
  showModifyForm: function (model) {
    this.modelToEdit = model;
    this.$editContent.val(model.get("content"));
    this.$commentEdit.show();
  },
  closeModifyForm: function () {
    this.modelToEdit = undefined;
    this.$commentEdit.hide();
    this.$editPasswd.val("");
    this.$editContent.val("");
  },
  handleInputError: function(elem) {
    elem.addClass("error");
    setTimeout(function () {
      elem.removeClass("error");
    }, 2000);
  },
  showUnknownError: function () {
    
  }
});

$(function () {
  new app.VisitorBookView();
});
