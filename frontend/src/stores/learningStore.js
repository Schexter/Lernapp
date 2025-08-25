"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.useLearningStore = void 0;
var zustand_1 = require("zustand");
exports.useLearningStore = (0, zustand_1.create)(function (set) { return ({
    currentQuestion: null,
    questionIndex: 0,
    totalQuestions: 0,
    userAnswers: {},
    showExplanation: false,
    setCurrentQuestion: function (question, index, total) {
        return set({ currentQuestion: question, questionIndex: index, totalQuestions: total });
    },
    setUserAnswer: function (questionId, answer) {
        return set(function (state) {
            var _a;
            return ({
                userAnswers: __assign(__assign({}, state.userAnswers), (_a = {}, _a[questionId] = answer, _a))
            });
        });
    },
    toggleExplanation: function () {
        return set(function (state) { return ({ showExplanation: !state.showExplanation }); });
    },
    reset: function () {
        return set({ userAnswers: {}, questionIndex: 0, currentQuestion: null, showExplanation: false });
    },
}); });
